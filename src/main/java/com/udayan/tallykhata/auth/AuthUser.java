package com.udayan.tallykhata.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.udayan.tallykhata.user.deserializer.DateOfBirthDeserializer;
import jakarta.validation.constraints.*;
import lombok.Data;

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
        private String password;
        @Email(message = "{email.notvalid}")
        @NotEmpty(message = "{email.notempty}")
        private String email;
//        @NotEmpty(message = "{mobileno.notempty}")
//        @Size(min = 11, max = 11, message = "{mobileno.size}")
//        @Pattern(regexp = "01[3-9]\\d{8}$",message = "{mobileno.pattern}")
        private String mobileNo;
//        @NotEmpty(message = "{gender.notempty}")
        private String gender;
        @NotEmpty(message = "{name.notempty}")
        private String fullName;
        @NotNull(message = "{dateofbirth.notnull}")
        @Past(message = "{dateofbirth.past}")
        @JsonDeserialize(using= DateOfBirthDeserializer.class )
        private LocalDate dateOfBirth;
    }

    @Data
    public static class VerifyUserRequest {
        @NotEmpty(message = "{username.notempty}")
        @Size(min = 4, max = 50, message = "{username.size}")
        private String username;
        @NotEmpty(message = "{otpcode.notempty}")
        @Size(min = 6, max = 12, message = "{otpcode.size}")
        private String otpCode;
    }

    @Data
    public static class ResendOTPRequest {
        @NotEmpty(message = "{username.notempty}")
        @Size(min = 4, max = 50, message = "{username.size}")
        private String username;
    }
}
