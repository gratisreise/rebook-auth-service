package com.example.rebookauthservice.domain.model.dto.response;

import lombok.Builder;

@Builder
public record RefreshResponse (String accessToken, String refreshToken){
    public static RefreshResponse of(String accessToken, String refreshToken){
        return RefreshResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
