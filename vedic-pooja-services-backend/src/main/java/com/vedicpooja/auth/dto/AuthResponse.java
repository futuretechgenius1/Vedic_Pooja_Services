package com.vedicpooja.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private UserInfo user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String role;
    }
}