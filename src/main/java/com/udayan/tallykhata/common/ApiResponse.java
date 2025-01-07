package com.udayan.tallykhata.common;

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
        USER_ALREADY_VERIFIED(301), USER_NOT_VERIFIED(302);
        final int value;
        BusinessCode(int value){
            this.value=value;
        }

    }
}
