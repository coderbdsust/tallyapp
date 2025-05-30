package com.udayan.tallyapp.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.udayan.tallyapp.user.GenderType;
import com.udayan.tallyapp.user.deserializer.DatePatternDeserializer;
import com.udayan.tallyapp.validator.EmailOrUsername;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

public class AuthUser {
    @Data
    public static class UserRequest {
        private UUID id;
        @NotEmpty(message = "{username.notempty}")
        @Size(min = 4, max = 50, message = "{username.size}")
        @Pattern(regexp = "^(?!.*[._]{2})[a-zA-Z0-9](?!.*[._]$)[a-zA-Z0-9._]{2,20}[a-zA-Z0-9]$",message = "{username.pattern}")
        private String username;
        @NotEmpty(message = "{password.notempty}")
        @Size(min = 8, max = 30, message = "{password.size}")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @ToString.Exclude
        private String password;
        @Email(message = "{email.notvalid}")
        @NotEmpty(message = "{email.notempty}")
        private String email;
//        @NotEmpty(message = "{mobileno.notempty}")
//        @Size(min = 11, max = 11, message = "{mobileno.size}")
//        @Pattern(regexp = "01[3-9]\\d{8}$",message = "{mobileno.pattern}")
        private String mobileNo;
//        @NotEmpty(message = "{gender.notempty}")
        @Enumerated(EnumType.STRING)
        private GenderType gender;
        @NotEmpty(message = "{name.notempty}")
        private String fullName;
        @NotNull(message = "{dateofbirth.notnull}")
        @Past(message = "{dateofbirth.past}")
        @JsonDeserialize(using= DatePatternDeserializer.class )
        private LocalDate dateOfBirth;
    }

    @Data
    public static class VerifyUserRequest {
        @NotEmpty(message = "{username.notempty}")
        @Size(min = 4, max = 50, message = "{username.size}")
        @EmailOrUsername(message = "{usernameOrEmail.format}")
        private String username;
        @NotEmpty(message = "{otpcode.notempty}")
        @Size(min = 6, max = 12, message = "{otpcode.size}")
        private String otpCode;
    }

    @Data
    public static class ResendOTPRequest {
        @NotEmpty(message = "{username.notempty}")
        @Size(min = 4, max = 50, message = "{username.size}")
        @EmailOrUsername(message = "{usernameOrEmail.format}")
        private String username;
    }
}
