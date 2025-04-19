package com.udayan.tallyapp.fileuploader;


import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.customexp.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

    @Value("${application.file.upload.base.url}")
    public String fileUploadServerBaseURL;

    @Value("${application.file.upload.base.directory}")
    public String fileUploadServerBaseDirectory;

    public FileDTO.FileResponse uploadFile(MultipartFile file){
        String fileName = file.getOriginalFilename();

        try{
            String newFileName = getNewFileName(fileName);
            log.debug("File uploading server base URL: {}",fileUploadServerBaseURL);
            String fileURL = fileUploadServerBaseURL+newFileName;
            log.debug("File URL : {}",fileURL);

            String fileWriteDirectory = fileUploadServerBaseDirectory+newFileName;
            log.debug("File write directory: {}",fileWriteDirectory);

            file.transferTo(new File(fileWriteDirectory));

            return FileDTO.FileResponse
                    .builder()
                    .fileName(newFileName)
                    .fileURL(fileURL)
                    .build();
        } catch (Throwable e){
            log.error("File upload error"+e);
            throw new FileUploadingException("Couldn't upload file");
        }
    }

    public String getNewFileName(String fileName){
        return UUID.randomUUID().toString()+"."+getExtension(fileName);
    }

    public String getExtension(String fileName){
        String[] words = fileName.split("\\.");
        return words[words.length-1];
    }

    public ApiResponse deleteFile(String fileName){
        String path = fileUploadServerBaseDirectory+fileName;
        log.debug("File Path: {}",path);
        File file = new File(path);

        if(!file.exists()){
            throw new InvalidDataException("File not found");
        }

        boolean deleted = file.delete();

        if(deleted){
            return ApiResponse.builder()
                    .sucs(true)
                    .businessCode(ApiResponse.BusinessCode.OK.getValue())
                    .message("File is deleted successfully")
                    .build();
        }
        throw new InvalidDataException("Couldn't delete the file");
    }

    public boolean isValidURL(String path) {
        try {
            new URL(path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getFullURL(String fileName){
        if(fileName!=null && !isValidURL(fileName)){
            return  fileUploadServerBaseURL+fileName;
        }
        return fileName;
    }
}
