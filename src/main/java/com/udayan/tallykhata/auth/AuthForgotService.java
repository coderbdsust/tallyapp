package com.udayan.tallykhata.auth;

import com.udayan.tallykhata.common.ApiResponse;
import com.udayan.tallykhata.email.EmailService;
import com.udayan.tallykhata.email.EmailTemplateName;
import com.udayan.tallykhata.user.User;
import com.udayan.tallykhata.user.UserRepository;
import com.udayan.tallykhata.user.exp.InvalidDataException;
import com.udayan.tallykhata.user.mapper.UserMapper;
import com.udayan.tallykhata.user.otp.OTPType;
import com.udayan.tallykhata.user.otp.UserOTP;
import com.udayan.tallykhata.user.otp.UserOTPRepository;
import com.udayan.tallykhata.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

    public ApiResponse doForgotPasswordByEmail(ForgotPassword.EmailRequest emailReq) {
        Optional<User> userEmailOptional = userRepository.findByEmail(emailReq.getEmail());
        if (userEmailOptional.isEmpty()) {
            return ApiResponse.builder()
                    .sucs(false)
                    .message("No user found using this email : " + emailReq.getEmail())
                    .userDetail(emailReq.getEmail())
                    .build();
        }
        UserOTP otp  = generateOTPForPasswordReset(userEmailOptional.get());
        return sendOTPForResetPasswordEmail(userEmailOptional.get(), otp);
    }

    private UserOTP generateOTPForPasswordReset(User user) {
        UserOTP otp = new UserOTP();
        otp.setOtp(Utils.generateOTP(6));
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(60));
        otp.setIsUsed(false);
        otp.setIsActive(true);
        otp.setOtpType(OTPType.PASSWORD_RESET.getName());
        otp.setUser(user);
        UserOTP saveOtp = userOTPRepository.save(otp);
        log.debug("{}", saveOtp);
        return saveOtp;

    }

    private ApiResponse sendOTPForResetPasswordEmail(User user, UserOTP otp) {
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
                    .message("OTP is send successfully for reset password")
                    .build();
        } catch (Exception e) {
            log.error("Email sending error ", e);
            return ApiResponse.builder()
                    .sucs(false)
                    .userDetail(user.getEmail())
                    .message("OTP sending error, Please try again")
                    .build();
        }
    }

    public ApiResponse resetPassword(ForgotPassword.ResetPassword resetPassword) throws InvalidDataException {

        if (!resetPassword.getPassword().matches(resetPassword.getConfirmPassword())) {
            return ApiResponse.builder()
                    .sucs(false)
                    .userDetail(resetPassword.getEmail())
                    .message("Password and Confirm Password didn't matched")
                    .build();

        }

        Optional<User> userOptional = userRepository.findByEmail(resetPassword.getEmail());
        if (userOptional.isEmpty()) {
            return ApiResponse.builder()
                    .sucs(false)
                    .userDetail(resetPassword.getEmail())
                    .message("No user found using email " + resetPassword.getEmail())
                    .build();
        }

        String saltedPassword = resetPassword.getPassword()+userOptional.get().getSalt();
        if(passwordEncoder.matches(saltedPassword, userOptional.get().getPassword())){
            throw new InvalidDataException("You used this password recently. Please choose a different one.");
        }

        Optional<UserOTP> otpOptional = userOTPRepository.findActiveOTPByUserIdAndCode(userOptional.get().getId(), resetPassword.getOtpCode(), OTPType.PASSWORD_RESET.getName());

        if (otpOptional.isEmpty()) {
            return ApiResponse.builder()
                    .sucs(false)
                    .userDetail(resetPassword.getEmail())
                    .message("Invalid OTP for Password Reset")
                    .build();
        }

        if(LocalDateTime.now().isAfter(otpOptional.get().getExpiryTime())){
            return ApiResponse.builder()
                    .sucs(false)
                    .userDetail(resetPassword.getEmail())
                    .message("OTP already expired")
                    .build();
        }

        User user = userOptional.get();
        UserOTP otp = otpOptional.get();
        otp.setIsActive(false);
        otp.setIsUsed(true);
        otp.setUpdatedDate(LocalDateTime.now());
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
                .message("Password reset successfully")
                .build();

    }
}
