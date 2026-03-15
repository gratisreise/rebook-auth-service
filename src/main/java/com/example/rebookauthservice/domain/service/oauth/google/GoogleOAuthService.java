package com.example.rebookauthservice.domain.service.oauth.google;

import com.example.rebookauthservice.common.annotation.OAuthServiceType;
import com.example.rebookauthservice.clientfeign.user.UserClient;
import com.example.rebookauthservice.common.enums.Provider;
import com.example.rebookauthservice.exception.CMissingDataException;
import com.example.rebookauthservice.domain.model.dto.OAuthRequest;
import com.example.rebookauthservice.domain.model.dto.OAuthUsersRequest;
import com.example.rebookauthservice.domain.model.dto.oauth.OAuthUserInfo;
import com.example.rebookauthservice.domain.model.dto.oauth.google.GoogleOAuthUserInfo;
import com.example.rebookauthservice.domain.model.dto.oauth.google.GoogleTokenResponse;
import com.example.rebookauthservice.domain.model.dto.oauth.google.GoogleUserInfo;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import com.example.rebookauthservice.domain.repository.AuthRepository;
import com.example.rebookauthservice.domain.service.oauth.AbstractOAuthService;
import com.example.rebookauthservice.common.security.JwtUtil;
import com.example.rebookauthservice.external.redis.RedisUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@OAuthServiceType(Provider.GOOGLE)
public class GoogleOAuthService extends AbstractOAuthService {
    @Value("${oauth2.client.google.client-id}")
    private String clientId;

    @Value("${oauth2.client.google.client-secret}")
    private String clientSecret;

    @Value("${oauth2.client.google.redirect-uri}")
    private String redirectUri;

    private final GoogleTokenClient googleTokenClient;
    private final GoogleUserClient googleUserClient;

    public GoogleOAuthService(
        UserClient userClient,
        JwtUtil jwtUtil,
        AuthRepository authRepository,
        RedisUtil redisUtil,
        GoogleTokenClient googleTokenClient,
        GoogleUserClient googleUserClient
    ){
        super(userClient, jwtUtil, authRepository, redisUtil);
        this.googleTokenClient = googleTokenClient;
        this.googleUserClient = googleUserClient;
    }

    @Override
    public String getAccessToken(OAuthRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("code", request.code());
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        GoogleTokenResponse response = googleTokenClient.getAccessToken(params);

        if(response == null){
            throw new CMissingDataException("토큰 응답이 비어있습니다.");
        }

        return response.getAccessToken();
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        GoogleUserInfo userInfo = googleUserClient.getUserInfo(setBearerAuth(accessToken));
        log.info("googleUserInfo: {}", userInfo);
        if(userInfo == null){
            throw new CMissingDataException("사용자 정보가 비어있습니다.");
        }
        return new GoogleOAuthUserInfo(userInfo);
    }

    @Override
    public String createOrGetUser(OAuthUserInfo userInfo) {
        Provider provider = Provider.GOOGLE;
        String providerId = userInfo.getId();
        Optional<AuthUser> user = authRepository
            .findByProviderAndProviderId(provider, providerId);
        if(user.isPresent()){
            return user.get().getUserId();
        }
        OAuthUsersRequest request = OAuthUsersRequest.from(userInfo);
        return userClient.createUser(request);
    }
}
