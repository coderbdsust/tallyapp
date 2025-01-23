package com.udayan.tallyapp.user.profile;

import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.user.User;
import com.udayan.tallyapp.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/users/v1")
@Slf4j
@RequiredArgsConstructor
public class ShortProfileController {

    @Autowired
    private UserService userService;

    @PostMapping("/short-profile/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addShortProfile(@Valid @RequestBody ShortProfileDTO.ShortProfileRequest request) throws InvalidDataException {
        log.debug("/users/v1/short-profile/add");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.addShortProfile(request, currentUser));
    }

    @PostMapping("/short-profile/add-list")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addShortProfile(@Valid @RequestBody ArrayList<ShortProfileDTO.ShortProfileRequest> requests) throws InvalidDataException {
        log.debug("/users/v1/short-profile/add-list");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.addShortProfile(requests, currentUser));
    }

    @DeleteMapping("/short-profile/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteAddress(@PathVariable("id") UUID id) throws InvalidDataException {
        log.debug("/users/v1/short-profile/{}",id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.deleteShortProfile(id, currentUser));
    }
}
