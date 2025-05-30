package com.udayan.tallyapp.auth;

import com.udayan.tallyapp.validator.EmailOrUsername;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class Login {

    public static enum LoginStatus {
        SUCCESS, TFA_CHANNEL_SELECTION, TFA_REQUIRED, ACCOUNT_LOCKED, PASSWORD_RESET_REQUIRED;
    }

    @Data
    public static class LoginRequest {
        @NotEmpty(message = "{usernameOrEmail.notempty}")
        @Size(min = 4, max = 50, message = "{usernameOrEmail.size}")
        @EmailOrUsername(message = "{usernameOrEmail.format}")
        private String username;
        @NotEmpty(message = "{password.notempty}")
        @Size(min = 8, max = 30, message = "{password.size}")
        private String password;
    }

    @Data
    @Builder
    public static class LoginResponse {
        private LoginStatus status;
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
        private LoginStatus status;
        private String fullName;
        private String username;
        private String email;
        private String role;
        private Date accessTokenExpiry;
        private Date refreshTokenExpiry;
    }

    @Data
    @Builder
    public static class TwoFaChannelRequiredResponse {
        private LoginStatus status;
        private String username;
        private Map<TFAProvider, String> otpChannels;
        private String token;
        private String message;
    }

    @Data
    @Builder
    public static class TwoFaRequiredResponse {
        private LoginStatus status;
        private String username;
        private TFAProvider channel;
        private String otpTxnId;
        private String message;
    }

    @Data
    @Builder
    public static class OtpVerificationRequest {
        @EmailOrUsername(message = "{usernameOrEmail.format}")
        private String username;
        @Size(min = 6, max = 6, message = "{otpcode.size}")
        private String otp;
        private UUID otpTxnId;
        @NotNull
        private TFAProvider channel;

    }

    @Data
    @Builder
    public static class LoginOtpRequest {
        @EmailOrUsername(message = "{usernameOrEmail.format}")
        private String username;
        private TFAProvider channel;
        private String token;
    }
}
