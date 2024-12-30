package com.udayan.tallykhata.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.udayan.tallykhata.user.deserializer.DateOfBirthDeserializer;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

public class AuthUser {
    @Data
    public static class UserRequest {
        private Long id;
        @NotEmpty(message = "Username cannot be empty")
        @Size(min = 4, max = 30, message = "Username length must be between 4 to 30")
        @Pattern(regexp = "^(?!.*[._]{2})[a-zA-Z0-9](?!.*[._]$)[a-zA-Z0-9._]{2,20}[a-zA-Z0-9]$",message = "Username pattern is invalid")
        private String username;
        @NotEmpty(message = "Password cannot be empty")
        @Size(min = 8, max = 16, message = "Password length must be between 8 to 16")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private String password;
        @Email(message = "Email should be valid")
        @NotEmpty(message = "Email cannot be empty")
        private String email;
        @NotEmpty(message = "Mobile number cannot be empty")
        @Size(min = 11, max = 11, message = "Mobile number length must be 11")
        @Pattern(regexp = "01[3-9]\\d{8}$",message = "Mobile number format not valid")
        private String mobileNo;
        @NotEmpty(message = "Name cannot be empty")
        private String fullName;
        @NotNull(message = "Date of Birth cannot be empty")
        @JsonDeserialize(using= DateOfBirthDeserializer.class )
        @Past(message = "Birth date must be in the past")
        private LocalDate dateOfBirth;
    }

    @Data
    public static class VerifyUserRequest {
        @NotEmpty(message = "Username cannot be empty")
        @Size(min = 4, max = 50, message = "Username length must be between 4 to 50")
        private String username;
        @NotEmpty(message = "Code cannot be empty")
        @Size(min = 6, max = 12, message = "Code length must be between 6 to 12")
        private String otpCode;
    }
}
