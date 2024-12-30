package com.udayan.tallykhata.auth;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

public class Login {

    @Data
    public static class LoginRequest {
        @NotEmpty(message = "Username can't be empty")
        @Size(min = 4, max = 30, message = "Username length must be between 4 to 30")
        private String username;
        @NotEmpty(message = "Password can't be empty")
        @Size(min = 8, max = 16, message = "Password length must be between 8 to 30")
        private String password;
    }

    @Data
    @Builder
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    public static class RefreshToken {
        @NotEmpty(message = "Refresh token can't be empty")
        private String refreshToken;
    }
}
