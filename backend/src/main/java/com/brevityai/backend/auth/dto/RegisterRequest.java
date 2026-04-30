package com.brevityai.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank
    public String username;

    @Email
    public String email;

    @Size(min = 6)
    public String password;
}
