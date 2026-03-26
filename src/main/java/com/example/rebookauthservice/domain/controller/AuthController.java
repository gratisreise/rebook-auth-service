package com.example.rebookauthservice.domain.controller;


import com.example.rebookauthservice.domain.model.dto.request.LoginRequest;
import com.example.rebookauthservice.domain.model.dto.request.OAuthRequest;
import com.example.rebookauthservice.domain.model.dto.request.RefreshRequest;
import com.example.rebookauthservice.domain.model.dto.request.SignUpRequest;
import com.example.rebookauthservice.domain.model.dto.response.TokenResponse;
import com.example.rebookauthservice.domain.service.AuthService;
import com.example.rebookauthservice.domain.service.oauth.OAuthService;
import com.example.rebookauthservice.domain.service.oauth.OAuthServiceFactory;
import com.rebook.common.core.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final OAuthServiceFactory oAuthServiceFactory;

    //test
    @GetMapping
    public String test(){
        return "테스트성공";
    }

    //회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<SuccessResponse<Void>> signUp(@Valid @RequestBody SignUpRequest request){
        authService.signUp(request);
        return SuccessResponse.toNoContent();
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request){
        return SuccessResponse.toOk(authService.login(request));
    }


    //소셜로그인
    @PostMapping("/oauth/login")
    public ResponseEntity<SuccessResponse<TokenResponse>> socialLogin(@Valid @RequestBody OAuthRequest request){
        OAuthService oAuthService = oAuthServiceFactory.getOAuthService(request.provider());
        return SuccessResponse.toOk(oAuthService.authenticate(request));
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<TokenResponse>> logout(
        @RequestHeader("Authorization") String accessToken) {
        authService.logout(accessToken);
        return SuccessResponse.toNoContent();
    }

    //리프레쉬
    @PostMapping("/refresh")
    public ResponseEntity<SuccessResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return SuccessResponse.toOk(authService.refresh(request.refreshToken()));
    }

}
