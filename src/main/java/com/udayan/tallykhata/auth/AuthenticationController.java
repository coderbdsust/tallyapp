package com.udayan.tallykhata.auth;


import com.udayan.tallykhata.customexp.*;
import com.udayan.tallykhata.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/v1")
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthForgotService authForgotService;

    @PostMapping("/register")
    public ResponseEntity<AuthUser.UserRequest> registerUser(@Valid @RequestBody AuthUser.UserRequest user) throws DuplicateKeyException, EmailSendingException {
        log.info("request to register user {}",user);
        AuthUser.UserRequest userRes = authService.registerUser(user);
        return ResponseEntity.ok().body(userRes);
    }

    @PostMapping("/resend-account-verification-otp")
    public ResponseEntity<ApiResponse> resendAccountVerificationOTP(@Valid @RequestBody AuthUser.ResendOTPRequest request) throws DuplicateKeyException, EmailSendingException {
        log.info("resend-verification-otp {}",request);
        ApiResponse response = authService.resendAccountVerificationOTP(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Login.LoginRequest loginRequest) throws UserNotActiveException, UserAccountIsLocked {
        log.info("request for login {}", loginRequest.getUsername());
        return ResponseEntity.ok().body(authService.doLogin(loginRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody Login.RefreshToken refreshToken) throws InvalidTokenException, UserNotActiveException, UserAccountIsLocked {
        log.info("request using refresh-token {}", refreshToken);
        return ResponseEntity.ok().body(authService.refreshToken(refreshToken));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyUser(@Valid @RequestBody AuthUser.VerifyUserRequest user) throws InvalidDataException {
        log.info("request to verify user {}",user);
        ApiResponse verifiedUser = authService.verifyUser(user);
        return ResponseEntity.ok().body(verifiedUser);
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyUser(@RequestParam(required = true) @NotBlank @Size(min=4, max = 20) String username, @RequestParam(required = true) @NotBlank @Size(min=6, max = 8) String otpCode) throws InvalidDataException {
        log.info("request to verify username {}",username);
        ApiResponse verifiedUser = authService.verifyUser(username, otpCode);
        return ResponseEntity.ok().body(verifiedUser);
    }

    @PostMapping("/forgot-password-by-email")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPassword.EmailRequest emailReq) throws InvalidDataException, EmailSendingException {
        log.info("request to forgot password by email {}",emailReq);
        ApiResponse res = authForgotService.sendForgotPasswordRequestByEmail(emailReq);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ForgotPassword.ResetPassword resetPassReq) throws InvalidDataException {
        log.info("request to reset password {}",resetPassReq.getEmail());
        ApiResponse res = authForgotService.resetPassword(resetPassReq);
        return ResponseEntity.ok().body(res);
    }
}
