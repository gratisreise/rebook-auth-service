package com.example.rebookauthservice.domain.model.dto.oauth.kako;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Properties(
    @JsonProperty("profile_image") String profileImage
){ }
