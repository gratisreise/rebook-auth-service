package com.example.rebookauthservice.domain.service.oauth;

import com.example.rebookauthservice.clientfeign.user.UserClient;
import com.example.rebookauthservice.domain.model.dto.OAuthRequest;
import com.example.rebookauthservice.domain.model.dto.TokenResponse;
import com.example.rebookauthservice.domain.model.dto.oauth.OAuthUserInfo;
import com.example.rebookauthservice.domain.repository.AuthRepository;
import com.example.rebookauthservice.common.security.JwtUtil;
import com.example.rebookauthservice.external.redis.RedisUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractOAuthService implements OAuthService{

    protected final UserClient userClient;
    protected final JwtUtil jwtUtil;
    protected final AuthRepository authRepository;
    protected final RedisUtil redisUtil;

    @Override
    public TokenResponse login(OAuthRequest request){
        //OAuthServer 접근토큰 요청
        String accessToken = getAccessToken(request);

        //유저정보 요청
        OAuthUserInfo userInfo = getUserInfo(accessToken);

        //유저서비스 유저생성 요청
        String userId = createOrGetUser(userInfo);

        String jwtToken = jwtUtil.createAccessToken(userId);
        String refreshToken = jwtUtil.createRefreshToken(userId);

        //리프레쉬 토큰 저장
        redisUtil.save(userId, refreshToken);

        return new TokenResponse(jwtToken, refreshToken);
    }


    protected String setBearerAuth(String accessToken){
        return "Bearer " + accessToken;
    }

}
