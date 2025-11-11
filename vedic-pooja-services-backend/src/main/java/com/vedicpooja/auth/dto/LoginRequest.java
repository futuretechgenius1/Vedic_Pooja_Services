package com.vedicpooja.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String phone;

    @NotBlank
    private String password;
}