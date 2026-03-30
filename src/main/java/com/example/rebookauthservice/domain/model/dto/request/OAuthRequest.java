package com.example.rebookauthservice.domain.model.dto.request;


import com.example.rebookauthservice.common.enums.OauthProvider;
import com.example.rebookauthservice.common.enums.Provider;
import com.example.rebookauthservice.common.exception.AuthException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import org.hibernate.validator.constraints.Length;

public record OAuthRequest (
    @NotBlank OauthProvider provider,
    @NotBlank String code
){


}
