package com.example.rebookauthservice.domain.model.dto.oauth.naver;

public record NaverUserInfo(
    String resultcode,
    String message,
    NaverResponse response
) { }
