package com.example.rebookauthservice.domain.service.oauth.naver;


import com.example.rebookauthservice.common.annotation.OAuthServiceType;
import com.example.rebookauthservice.clientfeign.user.UserClient;
import com.example.rebookauthservice.common.enums.Provider;
import com.example.rebookauthservice.exception.CMissingDataException;
import com.example.rebookauthservice.domain.model.dto.OAuthRequest;
import com.example.rebookauthservice.domain.model.dto.OAuthUsersRequest;
import com.example.rebookauthservice.domain.model.dto.oauth.OAuthUserInfo;
import com.example.rebookauthservice.domain.model.dto.oauth.naver.NaverOAuthUserInfo;
import com.example.rebookauthservice.domain.model.dto.oauth.naver.NaverTokenResponse;
import com.example.rebookauthservice.domain.model.dto.oauth.naver.NaverUserInfo;
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
@OAuthServiceType(Provider.NAVER)
public class NaverOAuthService extends AbstractOAuthService {


    @Value("${oauth2.client.naver.client-id}")
    private String clientId;

    @Value("${oauth2.client.naver.client-secret}")
    private String clientSecret;

    @Value("${oauth2.client.naver.redirect-uri}")
    private String redirectUri;

    private final NaverTokenClient naverTokenClient;
    private final NaverUserClient naverUserClient;

    public NaverOAuthService(
        UserClient userClient,
        JwtUtil jwtUtil,
        AuthRepository authRepository,
        RedisUtil redisUtil,
        NaverTokenClient naverTokenClient,
        NaverUserClient naverUserClient
    ){
        super(userClient, jwtUtil, authRepository, redisUtil);
        this.naverTokenClient = naverTokenClient;
        this.naverUserClient = naverUserClient;
    }


    @Override
    public String getAccessToken(OAuthRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("code", request.code());
        params.put("state", request.state());
        params.put("redirect_uri", redirectUri);

        NaverTokenResponse response = naverTokenClient.getAccessToken(params);

        if (response == null || response.getAccessToken() == null) {
            throw new CMissingDataException("토큰 응답이 비어있습니다.");
        }

        return response.getAccessToken();
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        NaverUserInfo userInfo = naverUserClient.getUserInfo(setBearerAuth(accessToken));

        if (userInfo == null || userInfo.response() == null) {
            throw new CMissingDataException("사용자 정보가 비어있습니다.");
        }

        return new NaverOAuthUserInfo(userInfo);
    }

    @Override
    public String createOrGetUser(OAuthUserInfo userInfo) {
        Provider provider = Provider.NAVER;
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
