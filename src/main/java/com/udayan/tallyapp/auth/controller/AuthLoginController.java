package com.udayan.tallyapp.auth.controller;


import com.udayan.tallyapp.auth.AuthService;
import com.udayan.tallyapp.auth.Login;
import com.udayan.tallyapp.customexp.InvalidTokenException;
import com.udayan.tallyapp.customexp.UserAccountIsLocked;
import com.udayan.tallyapp.customexp.UserNotActiveException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class AuthLoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Login.LoginRequest loginRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws UserNotActiveException, UserAccountIsLocked, InvalidTokenException {
        log.info("request for login {}", loginRequest.getUsername());
        return ResponseEntity.ok().body(authService.doLogin(loginRequest, httpRequest, httpResponse));
    }

    @PostMapping("/verify-login-otp")
    public ResponseEntity<?> verifyLoginOtp(@Valid @RequestBody Login.OtpVerificationRequest otpRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws UserNotActiveException, UserAccountIsLocked, InvalidTokenException {
        log.info("request for verify-login-otp {}", otpRequest);
        return ResponseEntity.ok().body(authService.verifyLoginOtp(otpRequest, httpRequest, httpResponse));
    }

    @PostMapping("/send-login-otp")
    public ResponseEntity<?> sendLoginOtp(@Valid @RequestBody Login.LoginOtpRequest loginOtpRequest) throws UserNotActiveException, UserAccountIsLocked, InvalidTokenException {
        log.info("request for resend-login-otp {}", loginOtpRequest.getUsername());
        return ResponseEntity.ok().body(authService.loginOtpRequest(loginOtpRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws InvalidTokenException, UserNotActiveException, UserAccountIsLocked {
        log.info("request using refresh-token");
        return ResponseEntity.ok().body(authService.refreshToken(request, response));
    }
}
