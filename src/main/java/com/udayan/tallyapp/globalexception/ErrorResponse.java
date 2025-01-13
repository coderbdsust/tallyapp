package com.udayan.tallykhata.globalexception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String message;
    private String error;
    private String path;
    private List<HashMap<String, String>> errors = new ArrayList<>();
}
