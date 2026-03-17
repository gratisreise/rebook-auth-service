package com.example.rebookauthservice.domain.service;

import com.example.rebookauthservice.clientfeign.user.UserClient;
import com.example.rebookauthservice.clientfeign.user.UsersCreateRequest;
import com.example.rebookauthservice.common.exception.AuthException;
import com.example.rebookauthservice.common.security.JwtProvider;
import com.example.rebookauthservice.domain.model.dto.request.LoginRequest;
import com.example.rebookauthservice.domain.model.dto.request.OAuthRequest;
import com.example.rebookauthservice.domain.model.dto.request.SignUpRequest;
import com.example.rebookauthservice.domain.model.dto.response.TokenResponse;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import com.example.rebookauthservice.domain.service.reader.AuthReader;
import com.example.rebookauthservice.domain.service.writer.AuthWriter;
import com.example.rebookauthservice.external.redis.BlacklistService;
import com.example.rebookauthservice.external.redis.RefreshService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthReader authReader;
    private final AuthWriter authWriter;
    private final PasswordEncoder encoder;
    private final UserClient userClient;
    private final JwtProvider jwtProvider;
    private final RefreshService refreshService;
    private final BlacklistService blacklistService;

    @Transactional
    public void signUp(SignUpRequest request) {
        //중복검증
        if(authReader.existsByUsername(request.username())){
            throw AuthException.unknown("존재하는 아이디입니다.");
        }

        //User 생성 요청
        UsersCreateRequest req = UsersCreateRequest.from(request);
        String userId = userClient.createUser(req);

        log.info("userId: {}", userId);

        //AuthUser 생성 및 저장
        AuthUser user = request.toEntity(userId, encoder);

        authWriter.save(user);
    }

    //로그인
    public TokenResponse login(LoginRequest request){
        //유저검증
        AuthUser user =  authReader.findByUsername(request.username());
        String encodedPassword = encoder.encode(request.password());
        validatePassword(encodedPassword, user.getPassword());

        String userId = user.getUserId();
        return generateToken(userId);
    }

    // 소셜 로그인
    public TokenResponse oauthLogin(OAuthRequest request) {
        AuthUser user = authReader.findByProviderAndProviderId(
            request.provider(), request.providerId()
        );
        if(!user.isEmpty()) {
            return generateToken(user.getUserId());
        }

        //User 생성 요청
        UsersCreateRequest req = UsersCreateRequest.from(request);
        String userId = userClient.createUser(req);
        return generateToken(userId);
    }


    //토큰 리프레쉬
    public TokenResponse refresh(String refreshToken) {
        String userId = jwtProvider.getRefreshUserId(refreshToken);
        return generateToken(userId);
    }

    //로그아웃
    public void logout(String accessToken){
        blacklistService.addBlack(accessToken);
    }

    // ==  == \\
    private void validatePassword(String encodedPassword, String password) {
        if(encoder.matches(encodedPassword, password)){
            throw AuthException.unknown("비밀번호가 틀립니다.");
        }
    }

    private TokenResponse generateToken(String userId){
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        refreshService.save(userId, refreshToken);

        return TokenResponse.of(accessToken, refreshToken);
    }
}
