package com.udayan.tallyapp.auth;

import com.udayan.tallyapp.validator.EmailOrUsername;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

public class ForgotPassword {

    @Data
    public static class EmailRequest {
        @NotEmpty(message = "{email.notempty}")
        @NotNull(message = "{email.notempty}")
        @EmailOrUsername(message = "{usernameOrEmail.format}")
        private String email;
    }

    @Data
    public static class OtpRequest {
        @NotEmpty(message = "{email.notempty}")
        @NotNull(message = "{email.notempty}")
        @EmailOrUsername(message = "{usernameOrEmail.format}")
        private String email;
        @Size(min = 6, max = 6, message = "{otpcode.size}")
        private String otpCode;
    }

    @Data
    public static class MobileRequest {
        @NotEmpty(message = "{mobileno.notempty}")
        @NotNull(message = "{mobileno.notempty}")
        @Size(min = 11, max = 11, message = "{mobileno.size}")
        @Pattern(regexp = "01[3-9]\\d{8}$", message = "{mobileno.pattern}")
        private String mobileNo;
    }

    @Data
    @Builder
    public static class ResetPassword {
        @NotEmpty(message = "{email.notempty}")
        @EmailOrUsername(message = "{usernameOrEmail.format}")
        String email;
        @Size(min = 6, max = 6, message = "{otpcode.size}")
        private String otpCode;
        @NotEmpty(message = "{password.notempty}")
        @Size(min = 8, max = 30, message = "{password.size}")
        private String password;
        @NotEmpty(message = "{confirmpassword.notempty}")
        @Size(min = 8, max = 30, message = "{confirmpassword.size}")
        private String confirmPassword;
    }

}
