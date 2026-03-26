package com.example.rebookauthservice.domain.service.oauth;


import com.example.rebookauthservice.domain.model.dto.request.OAuthRequest;
import com.example.rebookauthservice.domain.model.dto.response.TokenResponse;

public interface OAuthService {
  TokenResponse authenticate(OAuthRequest request);
}
