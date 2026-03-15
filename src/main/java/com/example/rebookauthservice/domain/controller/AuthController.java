package com.example.rebookauthservice.domain.controller;


import com.example.rebookauthservice.common.CommonResult;
import com.example.rebookauthservice.common.ResponseService;
import com.example.rebookauthservice.common.SingleResult;
import com.example.rebookauthservice.domain.model.dto.LoginRequest;
import com.example.rebookauthservice.domain.model.dto.OAuthRequest;
import com.example.rebookauthservice.domain.model.dto.RefreshRequest;
import com.example.rebookauthservice.domain.model.dto.RefreshResponse;
import com.example.rebookauthservice.domain.model.dto.SignUpRequest;
import com.example.rebookauthservice.domain.model.dto.TokenResponse;
import com.example.rebookauthservice.domain.repository.AuthRepository;
import com.example.rebookauthservice.domain.service.AuthService;
import com.example.rebookauthservice.domain.service.oauth.OAuthService;
import com.example.rebookauthservice.domain.service.oauth.OAuthServiceFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final OAuthServiceFactory oAuthServiceFactory;
    private final AuthRepository authRepository;

    //test
    @GetMapping
    public String test(){
        return "테스트성공";
    }

    //회원가입
    @PostMapping("/sign-up")
    public CommonResult signUp(@Valid @RequestBody SignUpRequest request){
        authService.signUp(request);
        return ResponseService.getSuccessResult();
    }

    //로그인
    @PostMapping("/login")
    public SingleResult<TokenResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseService.getSingleResult(authService.login(request));
    }

    //소셜로그인
    @GetMapping("/oauth/login")
    public SingleResult<TokenResponse> socialLogin(@Valid @RequestBody OAuthRequest request){
        OAuthService oauthService = oAuthServiceFactory.getOAuthService(request.provider());
        return ResponseService.getSingleResult(oauthService.login(request));
    }

    //리프레쉬
    @GetMapping("/refresh")
    public SingleResult<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseService.getSingleResult(authService.refresh(request.refreshToken()));
    }

}
