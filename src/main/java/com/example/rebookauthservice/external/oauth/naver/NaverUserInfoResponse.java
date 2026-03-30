package com.example.rebookauthservice.external.oauth.naver;

import com.example.rebookauthservice.common.enums.OauthProvider;
import com.example.rebookauthservice.common.enums.Role;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import com.example.rebookauthservice.external.oauth.OAuthUserInfo;
import com.fasterxml.jackson.annotation.JsonProperty;


public record NaverUserInfoResponse(@JsonProperty("response") NaverProfile response)
    implements OAuthUserInfo {

  @Override
  public String id() {
    return response != null ? response.id() : null;
  }

  @Override
  public String name() {
    return response != null ? response.nickname() : null;
  }

  @Override
  public String profileImage() {
    return response != null ? response.profileImage() : null;
  }

  @Override
  public AuthUser toEntity(String userId) {
    return AuthUser.builder()
        .userId(userId)
        .username(this.response().id() + OauthProvider.NAVER)
        .role(Role.USER)
        .provider(OauthProvider.NAVER)
        .providerId(this.id())
        .build();
  }

  @Override
  public String getNickname() {
    return OauthProvider.NAVER + this.response().id();
  }

  public record NaverProfile(
      String id,
      String nickname,
      @JsonProperty("profile_image") String profileImage,
      String name) {}
}
