package com.example.rebookauthservice.domain.model.dto;


import com.example.rebookauthservice.common.annotation.Password;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
    @NotBlank
    String username,

    @Password
    String password
){

}
