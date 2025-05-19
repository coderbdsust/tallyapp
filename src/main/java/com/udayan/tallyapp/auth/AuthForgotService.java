package com.udayan.tallyapp.auth;

import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.customexp.EmailSendingException;
import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.email.EmailService;
import com.udayan.tallyapp.email.EmailTemplateName;
import com.udayan.tallyapp.redis.RedisRateLimitService;
import com.udayan.tallyapp.redis.exp.TooManyRequestException;
import com.udayan.tallyapp.user.User;
import com.udayan.tallyapp.user.UserRepository;
import com.udayan.tallyapp.user.mapper.UserMapper;
import com.udayan.tallyapp.user.otp.OTPType;
import com.udayan.tallyapp.user.otp.UserOTP;
import com.udayan.tallyapp.user.otp.UserOTPRepository;
import com.udayan.tallyapp.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthForgotService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserOTPRepository userOTPRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    RedisRateLimitService redisRateLimitService;

    @Value("${application.account.password.reset.otp.expiration.minute}")
    private long passwordResetOTPExpirationInMinute;

    @Transactional
    public ApiResponse sendForgotPasswordRequestByEmail(ForgotPassword.EmailRequest emailReq) throws InvalidDataException, EmailSendingException {
        if (!redisRateLimitService.isForgotPasswordOTPGenerateAllowed(emailReq.getEmail())) {
            throw new TooManyRequestException("Too many request, Please wait and Try later");
        }

        User user = userRepository.findByUsernameOrEmail(emailReq.getEmail())
                .orElseThrow(() -> new InvalidDataException("No user found using this email"));
        userOTPRepository.revokeAllOTPByUserIDAndOtpType(user.getId(), OTPType.PASSWORD_RESET.getName());
        UserOTP otp = generateOTPForPasswordReset(user, OTPType.PASSWORD_RESET);
        return sendOTPForResetPasswordEmail(user, otp);
    }

    private UserOTP generateOTPForPasswordReset(User user, OTPType otpType) {
        UserOTP otp = new UserOTP();
        otp.setOtp(Utils.generateOTP(6));
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(passwordResetOTPExpirationInMinute));
        otp.setIsUsed(false);
        otp.setIsActive(true);
        otp.setOtpType(otpType.getName());
        otp.setUser(user);
        UserOTP saveOtp = userOTPRepository.save(otp);
        log.debug("{}", saveOtp);
        return saveOtp;

    }

    private ApiResponse sendOTPForResetPasswordEmail(User user, UserOTP otp) throws EmailSendingException {
        try {
            this.emailService.sendEmailForResetPassword(
                    user.getEmail(),
                    user.getUsername(),
                    user.getFullName(),
                    EmailTemplateName.FORGOT_PASSWORD_OTP,
                    otp.getOtp(),
                    "Reset Password"
            );
            otp.setIsSend(true);
            userOTPRepository.save(otp);
            return ApiResponse.builder()
                    .sucs(true)
                    .userDetail(user.getEmail())
                    .businessCode(ApiResponse.BusinessCode.OK.getValue())
                    .message("OTP is send successfully for reset password")
                    .build();
        } catch (Exception e) {
            log.error("Email sending error ", e);
            throw new EmailSendingException("Couldn't send otp by email");
        }
    }

    public ApiResponse resetPassword(ForgotPassword.ResetPassword resetPassword) throws InvalidDataException {

        if (!redisRateLimitService.isPasswordResetRequestAllowed(resetPassword.getEmail())) {
            throw new TooManyRequestException("Too many request, Please wait and Try later");
        }

        if (!resetPassword.getPassword().matches(resetPassword.getConfirmPassword())) {
            throw new InvalidDataException("Password and Confirm Password didn't matched");
        }

        User user = userRepository.findByUsernameOrEmail(resetPassword.getEmail())
                .orElseThrow(() -> new InvalidDataException("No user found using email or username"));


        String saltedPassword = resetPassword.getPassword() + user.getSalt();
        if (passwordEncoder.matches(saltedPassword, user.getPassword())) {
            throw new InvalidDataException("You used this password recently. Please choose a different one.");
        }

        UserOTP otp = userOTPRepository.findActiveOTPByUserIdAndCode(user.getId(), resetPassword.getOtpCode(), OTPType.PASSWORD_RESET.getName())
                .orElseThrow(() -> new InvalidDataException("Invalid OTP for Password Reset"));

        if (LocalDateTime.now().isAfter(otp.getExpiryTime())) {
            throw new InvalidDataException("OTP already expired");
        }

        otp.setIsUsed(true);
        otp.setIsActive(false);
       // otp.setUpdatedDate(LocalDateTime.now());
        otp.setValidatedTime(LocalDateTime.now());
        UserOTP savedOtp = userOTPRepository.save(otp);
        log.debug("Reset OTP updated : {}", savedOtp);

        user.setSalt(Utils.generateSalt(32));
        user.setPassword(passwordEncoder.encode(resetPassword.getPassword() + user.getSalt()));
        User savedUser = userRepository.save(user);
        log.debug("User password updated : {}", savedUser.getUsername());

        try {
            emailService.sendGenericEmailMessage(user.getEmail(),
                    user.getFullName(),
                    "Your account password reset successfully",
                    EmailTemplateName.GENERIC_MESSAGE_MAIL,
                    "Password Changed"
            );
        } catch (Exception e) {
            log.error("Mail sending error", e);
        }

        return ApiResponse.builder()
                .sucs(true)
                .userDetail(user.getUsername())
                .businessCode(ApiResponse.BusinessCode.OK.getValue())
                .message("Password reset successfully")
                .build();

    }

    public ApiResponse forgotPasswordOtpValidity(ForgotPassword.OtpRequest otpRequest) {

        if (!redisRateLimitService.isForgotPasswordOTPValidityAllowed(otpRequest.getEmail())) {
            throw new TooManyRequestException("Too many request, Please wait and Try later");
        }

        User user = userRepository.findByUsernameOrEmail(otpRequest.getEmail())
                .orElseThrow(() -> new InvalidDataException("No user found using this email or username"));

        UserOTP otp = userOTPRepository.findActiveOTPByUserIdAndCode(user.getId(), otpRequest.getOtpCode(), OTPType.PASSWORD_RESET.getName())
                .orElseThrow(() -> new InvalidDataException("Invalid OTP for Password Reset"));

        if (LocalDateTime.now().isAfter(otp.getExpiryTime())) {
            throw new InvalidDataException("OTP already expired");
        }

        return ApiResponse.builder()
                .sucs(true)
                .businessCode(ApiResponse.BusinessCode.OK.getValue())
                .userDetail(user.getEmail())
                .message("OTP is valid")
                .build();
    }
}
