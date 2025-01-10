package com.udayan.tallykhata.appproperty;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app-property/v1")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AppPropertyController {

    @Autowired
    AppPropertyService appPropertyService;

    @GetMapping("/list")
    public ResponseEntity<?> getApplicationProperties(@RequestParam(name = "page", defaultValue = "0", required = false) int page,
                                                      @RequestParam(name = "size", defaultValue = "10", required = false) int size,
                                                      @RequestParam(name = "search", defaultValue = "", required = false) String search){
       return ResponseEntity.ok(appPropertyService.getAllProperties(page, size, search));
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editApplicationProperty(@Valid @RequestBody  AppPropertyDTO.AppPropertyEditRequest request){
        return ResponseEntity.ok(appPropertyService.editAppProperty(request));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createApplicationProperty(@Valid @RequestBody  AppPropertyDTO.AppPropertyCreateRequest request){
        return ResponseEntity.ok(appPropertyService.createAppProperty(request));
    }
}
