package com.udayan.tallykhata.user.address;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class AddressDTO {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressRequest {
        private UUID id;
        @NotEmpty(message = "House name can't be empty")
        private String houseName;
        @NotEmpty(message = "Road name can't be empty")
        private String roadName;
        private String villageName;
        private String postOffice;
        @NotEmpty(message = "City name can't be empty")
        private String city;
        private String state;
        @NotEmpty(message = "Postcode can't be empty")
        @Pattern(regexp="\\d{4}", message = "Invalid post code, Must be 4 digit postcode")
        private String postCode;
        @NotEmpty(message = "Country name can't be empty")
        private String country;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressResponse {
        private UUID id;
        private String houseName;
        private String roadName;
        private String villageName;
        private String postOffice;
        private String city;
        private String state;
        private String postCode;
        private String country;
    }
}
