package com.example.rebookauthservice.clientfeign.user;

import com.example.rebookauthservice.domain.model.dto.request.OAuthRequest;
import com.example.rebookauthservice.domain.model.dto.request.SignUpRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record UsersCreateRequest (
    @NotBlank
    @Email
    String email,

    @NotBlank
    @Length(min = 3, max = 100)
    String nickname
){
    public static UsersCreateRequest from(SignUpRequest request){
        return UsersCreateRequest.builder()
            .email(request.email())
            .nickname(request.nickname())
            .build();
    }

    public static UsersCreateRequest from(OAuthRequest request){
        return UsersCreateRequest.builder()
            .email(request.getEmail())
            .nickname(request.getNickname())
            .build();
    }
}
