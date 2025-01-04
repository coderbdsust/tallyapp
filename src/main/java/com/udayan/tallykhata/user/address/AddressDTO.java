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
        @NotEmpty(message = "{address.addressLine.notempty}")
        private String addressLine;
        @NotEmpty(message = "{address.city.notempty}")
        private String city;
        private String state;
        @NotEmpty(message = "{address.postcode.notempty}")
        @Pattern(regexp="\\d{4}", message = "{address.postcode.pattern}")
        private String postCode;
        @NotEmpty(message = "{address.country.notempty}")
        private String country;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressResponse {
        private UUID id;
        private String addressLine;
        private String city;
        private String state;
        private String postCode;
        private String country;
    }
}
