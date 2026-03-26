package com.example.rebookauthservice.common.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OauthProvider {
  LOCAL("@rebook.com"),
  GOOGLE("@google.com"),
  KAKAO("@kakao.com"),
  NAVER("@naver.com");

  private final String emailSuffix;

  public String getEmail(){
      return this.name() + this.emailSuffix;
  }
}
