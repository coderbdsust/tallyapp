package com.udayan.tallyapp.fileuploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file-upload/v1")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class FileUploadController {

    @Autowired
    StorageService storageService;

    @PostMapping(path = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadFile(@RequestParam("fileName") MultipartFile file) {
        log.debug("/file-upload/v1/");
        return ResponseEntity.ok(storageService.uploadFile(file));
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable("fileName") String fileName) {
        log.debug("/file-upload/v1/{}",fileName);
        return ResponseEntity.ok(storageService.deleteFile(fileName));
    }
}
