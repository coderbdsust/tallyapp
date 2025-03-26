package com.udayan.tallyapp.user.shortprofile;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

public class ShortProfileDTO {

    @Data
    @Builder
    public static class ShortProfileResponse {
        private UUID id;
        private String designation;
        private String skills;
        private String companyName;
    }

    @Data
    @Builder
    public static class ShortProfileRequest {
        private UUID id;
        private String designation;
        private String skills;
        private String companyName;
    }
}
