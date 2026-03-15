package com.example.rebookauthservice.domain.model.dto.oauth.naver;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverResponse(
    String id, String nickname, String name,
    @JsonProperty("profile_image") String profileImage
) {}
