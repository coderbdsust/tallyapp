package com.udayan.tallyapp.auth.controller;


import com.udayan.tallyapp.auth.AuthForgotService;
import com.udayan.tallyapp.auth.ForgotPassword;
import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.customexp.EmailSendingException;
import com.udayan.tallyapp.customexp.InvalidDataException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
@Slf4j
public class AuthPasswordRecoveryController {

    @Autowired
    private AuthForgotService authForgotService;

    @PostMapping("/forgot-password-by-email")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPassword.EmailRequest emailReq) throws InvalidDataException, EmailSendingException {
        log.info("request to forgot password by email {}",emailReq);
        ApiResponse res = authForgotService.sendForgotPasswordRequestByEmail(emailReq);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/forgot-password-otp-validity")
    public ResponseEntity<?> forgotPasswordOtpValidity(@Valid @RequestBody ForgotPassword.OtpRequest otpRequest) throws InvalidDataException, EmailSendingException {
        log.info("request to forgot-password-otp-validity by email {}",otpRequest.getEmail());
        ApiResponse res = authForgotService.forgotPasswordOtpValidity(otpRequest);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ForgotPassword.ResetPassword resetPassReq) throws InvalidDataException {
        log.info("request to reset password {}",resetPassReq.getEmail());
        ApiResponse res = authForgotService.resetPassword(resetPassReq);
        return ResponseEntity.ok().body(res);
    }
}
