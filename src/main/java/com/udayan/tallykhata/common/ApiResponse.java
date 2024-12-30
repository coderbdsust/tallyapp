package com.udayan.tallykhata.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    private boolean sucs;
    private String message;
    private String userDetail;
}
