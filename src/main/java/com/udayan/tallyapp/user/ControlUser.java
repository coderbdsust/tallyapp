package com.udayan.tallyapp.user;

import com.udayan.tallyapp.validator.EmailOrUsername;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ControlUser {

    @Data
    public static class RevokeUserToken {
        @Size(min = 4, max = 50, message = "{username.size}")
        @EmailOrUsername
        private String username;
    }

    @Data
    public static class LockUser {
        @Size(min = 4, max = 50, message = "{username.size}")
        @EmailOrUsername
        private String username;
        @NotNull(message = "{accountlock.notempty}")
        private Boolean accountLocked;
    }

    @Data
    public static class ChangeUserRole {
        @Size(min = 4, max = 50, message = "{username.size}")
        @EmailOrUsername
        private  String username;
        @NotEmpty(message = "{role.notempty}")
        private String role;
    }
}
