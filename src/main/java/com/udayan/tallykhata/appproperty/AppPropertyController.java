package com.udayan.tallykhata.appproperty;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app-property/v1")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AppPropertyController {

    @Autowired
    AppPropertyService appPropertyService;

    @GetMapping("/list")
    public ResponseEntity<?> getApplicationProperties(){
       return ResponseEntity.ok(appPropertyService.getAllProperties());
    }
}
