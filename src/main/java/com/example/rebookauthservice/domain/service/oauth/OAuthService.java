package com.example.rebookauthservice.domain.service.oauth;


import com.example.rebookauthservice.domain.model.dto.OAuthRequest;
import com.example.rebookauthservice.domain.model.dto.TokenResponse;
import com.example.rebookauthservice.domain.model.dto.oauth.OAuthUserInfo;

public interface OAuthService {
    TokenResponse login(OAuthRequest oAuthRequest);
    String getAccessToken(OAuthRequest oAuthRequest);
    OAuthUserInfo getUserInfo(String accessToken);
    String createOrGetUser(OAuthUserInfo userInfo);
}
