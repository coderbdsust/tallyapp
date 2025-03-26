package com.udayan.tallyapp.auth;


import com.udayan.tallyapp.customexp.*;
import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.redis.RedisRateLimitService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    RedisRateLimitService redisRateLimitService;

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

    @Deprecated
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

    @GetMapping("/gender-list")
    public ResponseEntity<?> getGenderList(){
        log.debug("/auth/v1/gender-list");
        return ResponseEntity.ok(authService.getGenderList());
    }
}
