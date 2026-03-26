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
import com.example.rebookauthservice.external.oauth.naver.NaverApiClient;
import com.example.rebookauthservice.external.oauth.naver.NaverTokenResponse;
import com.example.rebookauthservice.external.oauth.naver.NaverUserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@OAuthServiceType(OauthProvider.NAVER)
public class NaverOAuthService implements OAuthService {
  private final AuthWriter authWriter;
  private final TokenService tokenService;
  private final UserClient userClient;
  private final NaverApiClient apiClient;

  @Override
  public TokenResponse authenticate(OAuthRequest request) {
    // 인증 코드를 액세스 토큰으로 교환
    NaverTokenResponse tokenResponse = apiClient.exchangeCodeForToken(request.code());

    // 제공업체에서 사용자 정보 조회
    NaverUserInfoResponse userInfo = apiClient.getUserInfo(tokenResponse.accessToken());

    // 유저 생성
    UsersCreateRequest req = UsersCreateRequest.of(
        OauthProvider.NAVER.getEmail(),  userInfo.getNickname());
    String userId = userClient.createUser(req);

    // 저장 및 수정
    AuthUser authUser = authWriter.saveOrUpdate(userInfo.toEntity(userId));

    // 토큰반환
    return tokenService.generateToken(authUser.getUserId());
  }
}
