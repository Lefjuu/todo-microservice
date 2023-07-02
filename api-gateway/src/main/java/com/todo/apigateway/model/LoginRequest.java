package com.todo.apigateway.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotNull
    @NotBlank
    @Email
    private String email;
    
    @NotNull
    @NotBlank
    private String password;
}
