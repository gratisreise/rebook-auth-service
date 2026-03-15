package com.example.rebookauthservice.domain.service.oauth.google;

import com.example.rebookauthservice.domain.model.dto.oauth.google.GoogleTokenResponse;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "googleToken", url = "https://oauth2.googleapis.com")
public interface GoogleTokenClient {
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    GoogleTokenResponse getAccessToken(@RequestBody Map<String, ?> formParams);
}

