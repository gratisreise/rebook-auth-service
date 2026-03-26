package com.example.rebookauthservice.external.oauth.google;


import com.example.rebookauthservice.common.enums.OauthProvider;
import com.example.rebookauthservice.common.enums.Role;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import com.example.rebookauthservice.external.oauth.OAuthUserInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfoResponse(
    String id, String name, @JsonProperty("picture") String profileImage) implements OAuthUserInfo {

  @Override
  public AuthUser toEntity(String userId) {
    return AuthUser.builder()
        .userId(userId)
        .username(this.id + OauthProvider.GOOGLE)
        .role(Role.USER)
        .provider(OauthProvider.GOOGLE)
        .providerId(this.id())
        .build();
  }

  @Override
  public String getNickname() {
    return OauthProvider.GOOGLE + this.id;
  }


}
