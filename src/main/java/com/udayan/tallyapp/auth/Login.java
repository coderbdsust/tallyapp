package com.udayan.tallyapp.auth;

import com.udayan.tallyapp.validator.EmailOrUsername;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

public class Login {

    @Data
    public static class LoginRequest {
        @NotEmpty(message = "{usernameOrEmail.notempty}")
        @Size(min = 4, max = 50, message = "{usernameOrEmail.size}")
        @EmailOrUsername
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
        private String refreshToken;
    }

    @Data
    @Builder
    public static class UserResponse {
        private String fullName;
        private String username;
        private String email;
        private String role;
        private Date accessTokenExpiry;
        private Date refreshTokenExpiry;
    }
}
