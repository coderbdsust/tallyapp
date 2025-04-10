package com.udayan.tallyapp.fileuploader;

import lombok.Builder;
import lombok.Data;

public class FileDTO {

    @Data
    @Builder
    public static class FileResponse{
        private String fileName;
        private String fileURL;
    }
}
