package com.udayan.tallyapp.appproperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

public class AppPropertyDTO {

    @Data
    public static class AppPropertyEditRequest {
        @NotNull(message = "App property id can't be empty")
        private UUID id;
        @NotEmpty(message = "App Key can't be empty")
        private String appKey;
        @NotEmpty(message = "App Value can't be empty")
        private String appValue;
        @NotEmpty(message = "App Profile can't be empty")
        private String profile;
    }

    @Data
    public static class AppPropertyCreateRequest {
        @NotEmpty(message = "App Key can't be empty")
        private String appKey;
        @NotEmpty(message = "App Value can't be empty")
        private String appValue;
        @NotEmpty(message = "App Profile can't be empty")
        private String profile;
    }
}
