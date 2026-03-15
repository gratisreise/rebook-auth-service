package com.example.rebookauthservice.domain.model.dto;


import com.example.rebookauthservice.common.enums.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OAuthRequest (
    @NotBlank
    String code,
    @NotNull
    Provider provider,
    @NotBlank
    String state
){

}
