package com.example.rebookauthservice.domain.service.oauth.kako;


import com.example.rebookauthservice.common.annotation.OAuthServiceType;
import com.example.rebookauthservice.clientfeign.user.UserClient;
import com.example.rebookauthservice.common.enums.Provider;
import com.example.rebookauthservice.exception.CMissingDataException;
import com.example.rebookauthservice.domain.model.dto.OAuthRequest;
import com.example.rebookauthservice.domain.model.dto.OAuthUsersRequest;
import com.example.rebookauthservice.domain.model.dto.oauth.OAuthUserInfo;
import com.example.rebookauthservice.domain.model.dto.oauth.kako.KakaoOAuthUserInfo;
import com.example.rebookauthservice.domain.model.dto.oauth.kako.KakaoTokenResponse;
import com.example.rebookauthservice.domain.model.dto.oauth.kako.KakaoUserInfo;
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
@OAuthServiceType(Provider.KAKAO)
public class KakaoOAuthService extends AbstractOAuthService {


    @Value("${oauth2.client.kakao.client-id}")
    private String clientId;

    @Value("${oauth2.client.kakao.client-secret}")
    private String clientSecret;

    @Value("${oauth2.client.kakao.redirect-uri}")
    private String redirectUri;

    private final KakaoTokenClient kakaoTokenClient;
    private final KakaoUserClient kakaoUserClient;


    public KakaoOAuthService(
        UserClient userClient,
        JwtUtil jwtUtil,
        AuthRepository authRepository,
        RedisUtil redisUtil,
        KakaoTokenClient kakaoTokenClient,
        KakaoUserClient kakaoUserClient
    ){
        super(userClient, jwtUtil, authRepository, redisUtil);
        this.kakaoTokenClient = kakaoTokenClient;
        this.kakaoUserClient = kakaoUserClient;
    }


    @Override
    public String getAccessToken(OAuthRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUri);
        params.put("code", request.code());
        params.put("client_secret", clientSecret);

        KakaoTokenResponse response = kakaoTokenClient.getAccessToken(params);

        if (response == null || response.getAccessToken() == null) {
            throw new CMissingDataException("토큰 응답이 비어있습니다.");
        }

        return response.getAccessToken();
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        KakaoUserInfo userInfo = kakaoUserClient.getUserInfo(setBearerAuth(accessToken));

        if (userInfo == null) {
            throw new CMissingDataException("카카오 유저정보가 비어있습니다.");
        }

        return new KakaoOAuthUserInfo(userInfo);
    }

    @Override
    public String createOrGetUser(OAuthUserInfo userInfo) {
        Provider provider = Provider.KAKAO;
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
