package com.udayan.tallyapp.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ControlUser {

    @Data
    public static class revokeUserToken {
        @Size(min = 4, max = 50, message = "{username.size}")
        @Pattern(regexp = "^(?!.*[._]{2})[a-zA-Z0-9](?!.*[._]$)[a-zA-Z0-9._]{2,20}[a-zA-Z0-9]$",message = "{username.pattern}")
        private String username;
    }

    @Data
    public static class LockUser {
        @Size(min = 4, max = 50, message = "{username.size}")
        @Pattern(regexp = "^(?!.*[._]{2})[a-zA-Z0-9](?!.*[._]$)[a-zA-Z0-9._]{2,20}[a-zA-Z0-9]$",message = "{username.pattern}")
        private String username;
        @NotNull(message = "{accountlock.notempty}")
        private Boolean accountLocked;
    }

    @Data
    public static class changeUserRole {
        @Size(min = 4, max = 50, message = "{username.size}")
        @Pattern(regexp = "^(?!.*[._]{2})[a-zA-Z0-9](?!.*[._]$)[a-zA-Z0-9._]{2,20}[a-zA-Z0-9]$",message = "{username.pattern}")
        private  String username;
        @NotEmpty(message = "{role.notempty}")
        private String role;
    }
}
