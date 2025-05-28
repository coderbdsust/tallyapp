package com.udayan.tallyapp.auth.controller;


import com.udayan.tallyapp.auth.AuthService;
import com.udayan.tallyapp.auth.AuthUser;
import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.customexp.DuplicateKeyException;
import com.udayan.tallyapp.customexp.EmailSendingException;
import com.udayan.tallyapp.customexp.InvalidDataException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/v1")
@Slf4j
public class AuthAccountController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody AuthUser.UserRequest user) throws DuplicateKeyException, EmailSendingException {
        log.info("request to register user {}",user);
        AuthUser.UserRequest userRes = authService.registerUser(user);
        return new ResponseEntity<>(userRes, HttpStatus.CREATED);
    }

    @PostMapping("/resend-account-verification-otp")
    public ResponseEntity<ApiResponse> resendAccountVerificationOTP(@Valid @RequestBody AuthUser.ResendOTPRequest request) throws DuplicateKeyException, EmailSendingException {
        log.info("resend-verification-otp {}",request);
        ApiResponse response = authService.resendAccountVerificationOTP(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/account-verify")
    public ResponseEntity<ApiResponse> verifyUserAccount(@Valid @RequestBody AuthUser.VerifyUserRequest user) throws InvalidDataException {
        log.info("request to verify user account {}",user);
        ApiResponse verifiedUser = authService.verifyUser(user);
        return ResponseEntity.ok().body(verifiedUser);
    }

    @GetMapping("/gender-list")
    public ResponseEntity<?> getGenderList(){
        log.debug("/auth/v1/gender-list");
        return ResponseEntity.ok(authService.getGenderList());
    }
}
