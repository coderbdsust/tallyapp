package com.udayan.tallyapp.common;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class ApiResponse {
    private boolean sucs;
    private String message;
    private String userDetail;
    private Integer businessCode;

    @Getter
    public enum BusinessCode {
        OK(200),
        USER_ALREADY_VERIFIED(601),
        USER_NOT_VERIFIED(602),
        RESOURCE_NOT_FOUND(604),
        MOBILE_NOT_VERIFIED(605),
        EMAIL_NOT_VERIFIED(607);

        final int value;

        BusinessCode(int value) {
            this.value = value;
        }
    }
}
