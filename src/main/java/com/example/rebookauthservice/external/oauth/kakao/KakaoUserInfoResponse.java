package com.example.rebookauthservice.external.oauth.kakao;

import com.example.rebookauthservice.common.enums.OauthProvider;
import com.example.rebookauthservice.common.enums.Role;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import com.example.rebookauthservice.external.oauth.OAuthUserInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponse(
    String id, @JsonProperty("kakao_account") KakaoProfile kakaoAccount) implements OAuthUserInfo {

  @Override
  public String name() {
    return kakaoAccount != null ? kakaoAccount.nickname() : null;
  }

  @Override
  public String profileImage() {
    return kakaoAccount != null ? kakaoAccount.profileImageUrl() : null;
  }

  @Override
  public AuthUser toEntity(String userId) {
    return AuthUser.builder()
        .userId(userId)
        .username(this.id + OauthProvider.KAKAO)
        .role(Role.USER)
        .provider(OauthProvider.KAKAO)
        .providerId(this.id())
        .build();
  }

  @Override
  public String getNickname() {
    return OauthProvider.KAKAO + this.id;
  }

  public record KakaoProfile(
      @JsonProperty("profile_nickname") String nickname,
      @JsonProperty("profile_image_url") String profileImageUrl) {}
}
