package com.example.rebookauthservice.domain.model.dto.request;

import com.example.rebookauthservice.common.annotation.Password;
import com.example.rebookauthservice.common.enums.Provider;
import com.example.rebookauthservice.common.enums.Role;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;


public record SignUpRequest(
    @NotBlank
    @Length(min=3, max=40)
    String username,

    @Password
    String password,

    @NotBlank
    @Length(min=3, max=15)
    String nickname,

    @NotBlank
    @Email
    String email
) {

    public AuthUser toEntity(String userId, PasswordEncoder encoder){
        return AuthUser.builder()
            .userId(userId)
            .username(username)
            .password(encoder.encode(password))
            .role(Role.USER)
            .provider(Provider.LOCAL)
            .build();
    }
}
