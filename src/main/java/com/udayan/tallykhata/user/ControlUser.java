package com.udayan.tallykhata.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ControlUser {

    @Data
    public static class revokeUserToken {
        @Size(min = 4, max = 30, message = "Username length must be between 4 to 30")
        @Pattern(regexp = "^(?!.*[._]{2})[a-zA-Z0-9](?!.*[._]$)[a-zA-Z0-9._]{2,20}[a-zA-Z0-9]$",message = "Username pattern is invalid")
        private String username;
    }

    @Data
    public static class LockUser {
        @Size(min = 4, max = 30, message = "Username length must be between 4 to 30")
        @Pattern(regexp = "^(?!.*[._]{2})[a-zA-Z0-9](?!.*[._]$)[a-zA-Z0-9._]{2,20}[a-zA-Z0-9]$",message = "Username pattern is invalid")
        private String username;
        @NotNull(message = "Account lock value can't be empty")
        private Boolean accountLocked;

    }

    @Data
    public static class changeUserRole {
        @Size(min = 4, max = 30, message = "Username length must be between 4 to 30")
        @Pattern(regexp = "^(?!.*[._]{2})[a-zA-Z0-9](?!.*[._]$)[a-zA-Z0-9._]{2,20}[a-zA-Z0-9]$",message = "Username pattern is invalid")
        private  String username;
        @NotEmpty(message = "Role can't be empty")
        private String role;
    }
}
