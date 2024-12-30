package com.udayan.tallykhata.auth;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

public class ForgotPassword {

    @Data
    public static class EmailRequest {
        @Email(message = "Invalid email address")
        @NotEmpty(message = "Email cannot be empty")
        @NotNull(message = "Email cannot be empty")
        private String email;
    }

    @Data
    public static class MobileRequest {
        @NotEmpty(message = "Mobile number cannot be empty")
        @NotNull(message = "Mobile number cannot be empty")
        @Size(min = 11, max = 11, message = "Mobile number length must be 11")
        @Pattern(regexp = "01[3-9]\\d{8}$", message = "Mobile number format not valid")
        private String mobileNo;
    }

    @Data
    @Builder
    public static class ResetPassword {
        @NotEmpty(message = "Email can't be empty")
        @Email(message = "Invalid email address")
        String email;
        @Size(min = 6, max = 8, message = "Invalid OTP length, Must be between 6 to 8")
        private String otpCode;
        @NotEmpty(message = "Password can't be empty")
        @Size(min = 8, max = 30, message = "Invalid Password length, Must be between 8 to 30")
        private String password;
        @NotEmpty(message = "Confirm Password can't be empty")
        @Size(min = 8, max = 30, message = "Invalid Password length, Must be between 8 to 30")
        private String confirmPassword;
    }

}
