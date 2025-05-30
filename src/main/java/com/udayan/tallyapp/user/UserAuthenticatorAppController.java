package com.udayan.tallyapp.user;

import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.user.authenticator.AuthenticatorAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/v1/authenticator")
@Slf4j
@RequiredArgsConstructor
public class UserAuthenticatorAppController {

    @Autowired
    AuthenticatorAppService authenticatorAppService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> authenticatorAppRegister() throws InvalidDataException {
        log.debug("/users/v1/authenticator/register");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(authenticatorAppService.authenticatorAppRegister(currentUser.getUsername()));
    }

    @PostMapping("/tfa-enable")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> authenticatorAppEnable(@Valid @RequestBody UserDTO.AuthenticatorTfaRequest request) throws InvalidDataException {
        log.debug("/users/v1/authenticator/enable");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(authenticatorAppService.enableTfa(request, currentUser.getUsername()));
    }

    @PostMapping("/tfa-disable")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> authenticatorAppDisable(@Valid @RequestBody UserDTO.AuthenticatorTfaRequest request) throws InvalidDataException {
        log.debug("/users/v1/authenticator/disable");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(authenticatorAppService.disableAuthenticatorApp(request, currentUser.getUsername()));
    }




}
