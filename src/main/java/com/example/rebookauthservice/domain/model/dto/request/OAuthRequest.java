package com.example.rebookauthservice.domain.model.dto.request;


import com.example.rebookauthservice.common.enums.Provider;
import com.example.rebookauthservice.common.exception.AuthException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import org.hibernate.validator.constraints.Length;

public record OAuthRequest (
    @NotBlank Provider provider,
    @NotBlank String providerId
){

    public String getEmail(){
        return switch(provider){
            case NAVER -> providerId + "@naver.com";
            case GOOGLE -> providerId + "@google.com";
            case KAKAO -> providerId + "@kakao.com";
            case LOCAL -> null;
        };
    }

    public String getNickname() {
        return "Nickname" + providerId;
    }
}
