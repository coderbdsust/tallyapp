package com.udayan.tallyapp.organization;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.udayan.tallyapp.employee.Status;
import com.udayan.tallyapp.user.deserializer.DatePatternDeserializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

public class OrganizationDTO {

    @Data
    @Builder
    public static class OrganizationRequest {
        private UUID id;
        @NotEmpty(message = "Organization name can't be empty")
        private String orgName;
        @NotEmpty(message = "Organization reg number can't be empty")
        private String orgRegNumber;
        @NotEmpty(message = "Organization TIN number can't be empty")
        private String orgTinNumber;
        private String orgVatNumber;
        @Pattern(regexp = "01[3-9]\\d{8}$",message = "Organization mobile number can't be empty")
        private String orgMobileNo;
        private String orgEmail;
        private String orgOpenAt;
        private String orgOpenInWeek;
        private String orgOpeningTitle;
        private String owner;
        private String image;
        private String avatar;
        @Past(message = "Organization start date must be from past")
        @JsonDeserialize(using= DatePatternDeserializer.class )
        private LocalDate since;
        @NotEmpty(message = "Organization address line can't be empty")
        private String orgAddressLine;
        @NotEmpty(message = "Organization address city can't be empty")
        private String orgAddressCity;
        @NotEmpty(message = "Organization address postcode can't be empty")
        private String orgAddressPostcode;
        @NotEmpty(message = "Organization address country can't be empty")
        private String orgAddressCountry;
        @NotNull(message = "Organization status can't be empty")
        private Status status;
    }
}
