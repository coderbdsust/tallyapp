package com.udayan.tallyapp.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ChangePassword {
    @Data
    public static class ChangeUserPasswordRequest {

        @NotEmpty(message = "{oldpassword.notempty}")
        @Size(min = 8, max = 30, message = "{oldpassword.size}")
        private String oldPassword;

        @NotEmpty(message = "{password.notempty}")
        @Size(min = 8, max = 30, message = "{password.size}")
        private String password;

        @NotEmpty(message = "{confirmpassword.notempty}")
        @Size(min = 8, max = 30, message = "{confirmpassword.size}")
        private String confirmPassword;
    }
}
