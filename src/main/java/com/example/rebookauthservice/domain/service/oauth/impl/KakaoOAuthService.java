package com.example.rebookauthservice.domain.service.oauth.impl;


import com.example.rebookauthservice.clientfeign.user.UserClient;
import com.example.rebookauthservice.clientfeign.user.UsersCreateRequest;
import com.example.rebookauthservice.common.annotation.OAuthServiceType;
import com.example.rebookauthservice.common.enums.OauthProvider;
import com.example.rebookauthservice.domain.model.dto.request.OAuthRequest;
import com.example.rebookauthservice.domain.model.dto.response.TokenResponse;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import com.example.rebookauthservice.domain.repository.AuthWriter;
import com.example.rebookauthservice.domain.service.TokenService;
import com.example.rebookauthservice.domain.service.oauth.OAuthService;
import com.example.rebookauthservice.external.oauth.kakao.KakaoApiClient;
import com.example.rebookauthservice.external.oauth.kakao.KakaoTokenResponse;
import com.example.rebookauthservice.external.oauth.kakao.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@OAuthServiceType(OauthProvider.KAKAO)
public class KakaoOAuthService implements OAuthService {

  private final AuthWriter authWriter;
  private final TokenService tokenService;
  private final UserClient userClient;
  private final KakaoApiClient apiClient;

  @Override
  public TokenResponse authenticate(OAuthRequest request) {
    // 인증 코드를 액세스 토큰으로 교환
    KakaoTokenResponse tokenResponse = apiClient.exchangeCodeForToken(request.code());

    // 제공업체에서 사용자 정보 조회
    KakaoUserInfoResponse userInfo = apiClient.getUserInfo(tokenResponse.accessToken());

    //유정 생성 및 조회
    UsersCreateRequest req = UsersCreateRequest.of(
        OauthProvider.KAKAO.getEmail(),  userInfo.getNickname());
    String userId = userClient.createUser(req);

    // 저장 및 수정
    AuthUser authUser = authWriter.saveOrUpdate(userInfo.toEntity(userId));

    // 토큰반환
    return tokenService.generateToken(authUser.getUserId());
  }
}
