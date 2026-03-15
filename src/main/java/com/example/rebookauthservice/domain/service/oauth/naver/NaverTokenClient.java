package com.example.rebookauthservice.domain.service.oauth.naver;


import com.example.rebookauthservice.domain.model.dto.oauth.naver.NaverTokenResponse;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "naverToken", url = "https://nid.naver.com")
public interface NaverTokenClient {
    @PostMapping(value = "/oauth2.0/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    NaverTokenResponse getAccessToken(@RequestBody Map<String, ?> params);
}
