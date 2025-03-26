package com.udayan.tallyapp.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.udayan.tallyapp.user.address.AddressDTO;
import com.udayan.tallyapp.user.deserializer.DatePatternDeserializer;
import com.udayan.tallyapp.user.shortprofile.ShortProfileDTO;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDTO {

    @Data
    @Builder
    public static class RegisteredUserResponse {
        private UUID id;
        private String username;
        private String email;
        private String mobileNo;
        private String fullName;
        private GenderType gender;
        private LocalDate dateOfBirth;
        private boolean enabled;
        private boolean accountLocked;
        private boolean isMobileNoVerified;
        private boolean tfaEnabled;
        private LocalDateTime createdDate;
        private ArrayList<String> roles;
        private List<AddressDTO.AddressResponse> addressList;
        private List<ShortProfileDTO.ShortProfileResponse> shortProfileList;
    }

    @Data
    @Builder
    public static class UserRequest {
        @NotNull(message = "{id.notnull}")
        private UUID id;
        @NotEmpty(message = "{mobileno.notempty}")
        @Size(min = 11, max = 11, message = "{mobileno.size}")
        @Pattern(regexp = "01[3-9]\\d{8}$",message = "{mobileno.pattern}")
        private String mobileNo;
        @NotEmpty(message = "{name.notempty}")
        private String fullName;
        @NotNull(message = "{gender.notempty}")
        private GenderType gender;
        @NotNull(message = "{dateofbirth.notnull}")
        @Past(message = "{dateofbirth.past}")
        @JsonDeserialize(using= DatePatternDeserializer.class )
        private LocalDate dateOfBirth;
    }

    @Data
    @Builder
    public static class UserForOrgResponse{
        private UUID id;
        private String fullName;
        private String email;
    }
}
