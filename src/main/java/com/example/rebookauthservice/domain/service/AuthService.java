package com.example.rebookauthservice.domain.service;

import com.example.rebookauthservice.clientfeign.user.UserClient;
import com.example.rebookauthservice.clientfeign.user.UsersCreateRequest;
import com.example.rebookauthservice.common.exception.AuthException;
import com.example.rebookauthservice.common.security.JwtProvider;
import com.example.rebookauthservice.domain.model.dto.request.LoginRequest;
import com.example.rebookauthservice.domain.model.dto.request.SignUpRequest;
import com.example.rebookauthservice.domain.model.dto.response.TokenResponse;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import com.example.rebookauthservice.domain.repository.AuthReader;
import com.example.rebookauthservice.domain.repository.AuthWriter;
import com.example.rebookauthservice.external.redis.RedisTokenService;
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
    private final RedisTokenService redisTokenService;

    @Transactional
    public void signUp(SignUpRequest request) {
        //중복검증
        if(authReader.existsByUsername(request.username())){
            throw AuthException.unknown("존재하는 아이디입니다.");
        }

        //User 생성 요청
        UsersCreateRequest req = UsersCreateRequest.from(request);
        String userId = userClient.createUser(req);

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
        return generateToken(user.getUserId());
    }


    //토큰 리프레쉬
    public TokenResponse refresh(String refreshToken) {
        return generateToken(jwtProvider.getRefreshUserId(refreshToken));
    }

    //로그아웃
    public void logout(String accessToken){
        long remainingTime = jwtProvider.getExpiration(accessToken);
        redisTokenService.addBlacklist(accessToken, remainingTime);
    }

    // == Private ==
    private void validatePassword(String encodedPassword, String password) {
        if(encoder.matches(encodedPassword, password)){
            throw AuthException.unknown("비밀번호가 틀립니다.");
        }
    }

    private TokenResponse generateToken(String userId){
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        redisTokenService.saveRefreshToken(userId, refreshToken);

        return TokenResponse.of(accessToken, refreshToken);
    }
}
