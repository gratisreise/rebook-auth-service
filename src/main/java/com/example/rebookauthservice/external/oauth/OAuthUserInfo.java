package com.example.rebookauthservice.external.oauth;


import com.example.rebookauthservice.domain.model.entity.AuthUser;

public interface OAuthUserInfo {

  String id();

  String name();

  String profileImage();

  AuthUser toEntity(String userId);

  String getNickname();


}
