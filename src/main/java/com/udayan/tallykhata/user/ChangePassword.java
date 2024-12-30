package com.udayan.tallykhata.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ChangePassword {
    @Data
    public static class ChangeUserPasswordRequest {

        @NotEmpty(message = "Current Password cannot be empty")
        @Size(min = 8, max = 16, message = "Current Password length must be between 8 to 30")
        private String currentPassword;

        @NotEmpty(message = "Password cannot be empty")
        @Size(min = 8, max = 16, message = "Password length must be between 8 to 30")
        private String password;

        @NotEmpty(message = "Confirm Password cannot be empty")
        @Size(min = 8, max = 16, message = "Confirm Password length must be between 8 to 30")
        private String confirmPassword;
    }
}
