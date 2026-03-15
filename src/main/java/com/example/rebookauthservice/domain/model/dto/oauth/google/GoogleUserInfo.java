package com.example.rebookauthservice.domain.model.dto.oauth.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfo(
    @JsonProperty("sub") String id,
    String email,
    String name,
    String picture
) { }
