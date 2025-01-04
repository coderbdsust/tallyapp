package com.udayan.tallykhata.auth;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

public class Login {

    @Data
    public static class LoginRequest {
        @NotEmpty(message = "{usernameOrEmail.notempty}")
        @Size(min = 4, max = 50, message = "{usernameOrEmail.size}")
        private String username;
        @NotEmpty(message = "{password.notempty}")
        @Size(min = 8, max = 30, message = "{password.size}")
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
        @NotEmpty(message = "{refreshtoken.notempty}")
        private String refreshToken;
    }
}
