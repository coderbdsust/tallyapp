package com.udayan.tallyapp.auth;

import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.customexp.*;
import com.udayan.tallyapp.email.EmailService;
import com.udayan.tallyapp.email.EmailTemplateName;
import com.udayan.tallyapp.redis.RedisRateLimitService;
import com.udayan.tallyapp.redis.RedisTokenService;
import com.udayan.tallyapp.redis.exp.TooManyRequestException;
import com.udayan.tallyapp.security.jwt.JwtService;
import com.udayan.tallyapp.user.User;
import com.udayan.tallyapp.user.UserRepository;
import com.udayan.tallyapp.user.otp.OTPType;
import com.udayan.tallyapp.user.otp.UserOTP;
import com.udayan.tallyapp.user.otp.UserOTPRepository;
import com.udayan.tallyapp.user.role.Role;
import com.udayan.tallyapp.user.role.RoleRepository;
import com.udayan.tallyapp.user.token.TokenService;
import com.udayan.tallyapp.user.token.TokenType;
import com.udayan.tallyapp.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserOTPRepository userOTPRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    RedisTokenService redisTokenService;

    @Autowired
    RedisRateLimitService redisRateLimitService;

    @Value("${application.mailing.activation-url}")
    private String accountActivationURL;

    @Value("${application.account.verification.otp.expiration.minute}")
    private long accountVerificationOTPExpirationMinute;

    @Transactional
    public AuthUser.UserRequest registerUser(AuthUser.UserRequest userRequest) throws DuplicateKeyException, EmailSendingException {
        Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("Role 'USER' not initiated correctly"));

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setSalt(Utils.generateSalt(32));
        user.setPassword(passwordEncoder.encode(userRequest.getPassword() + user.getSalt()));
        user.setMobileNo(userRequest.getMobileNo());
        user.setFullName(userRequest.getFullName());
        user.setDateOfBirth(userRequest.getDateOfBirth());
        user.setGender(userRequest.getGender());
        user.setRoles(List.of(userRole));
        user.setEnabled(false);
        user.setTfaEnabled(false);
        user.setIsMobileNumberVerified(false);

        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new DuplicateKeyException("Username already taken by user");
        }

        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new DuplicateKeyException("Email already taken by user");
        }

        user = userRepository.save(user);
        userRequest.setId(user.getId());

        UserOTP otp = generateOTPForUserVerification(user);
        sendAccountActivationEmail(user, otp);
        return userRequest;
    }

    @Transactional
    public ApiResponse resendAccountVerificationOTP(AuthUser.ResendOTPRequest request) {

        if(!redisRateLimitService.isResendAccountVerificationOTPAllowed(request.getUsername())) {
            throw new TooManyRequestException("Too many request, Please wait and try later");
        }

        User user = userRepository.findByUsernameOrEmail(request.getUsername())
                .orElseThrow(() -> new InvalidDataException("No user found using this param"));

        if (user.isEnabled()) {
            return ApiResponse.builder().sucs(true).message("User already verified")
                    .businessCode(ApiResponse.BusinessCode.USER_ALREADY_VERIFIED.getValue())
                    .userDetail(user.getEmail()).build();
        }

        userOTPRepository.revokeAllOTPByUserIDAndOtpType(user.getId(), OTPType.ACCOUNT_VERIFICATION.getName());

        UserOTP otp = generateOTPForUserVerification(user);

        sendAccountActivationEmail(user, otp);

        return ApiResponse.builder()
                .sucs(true)
                .message("New OTP generated, Please check email")
                .businessCode(ApiResponse.BusinessCode.USER_NOT_VERIFIED.getValue())
                .userDetail(user.getEmail())
                .build();
    }

    private UserOTP generateOTPForUserVerification(User user) {
        UserOTP otp = new UserOTP();
        otp.setOtp(Utils.generateOTP(6));
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(accountVerificationOTPExpirationMinute));
        otp.setIsUsed(false);
        otp.setIsActive(true);
        otp.setOtpType(OTPType.ACCOUNT_VERIFICATION.getName());
        otp.setUser(user);
        UserOTP saveOtp = userOTPRepository.save(otp);
        log.debug("{}", saveOtp);
        return saveOtp;
    }

    private void sendAccountActivationEmail(User user, UserOTP otp) throws EmailSendingException {
        try {
            this.emailService.sendEmailForActivateAccount(
                    user.getEmail(),
                    user.getUsername(),
                    user.getFullName(),
                    EmailTemplateName.ACTIVATE_ACCOUNT,
                    accountActivationURL,
                    otp.getOtp(),
                    "Account Verification"
            );
            otp.setIsSend(true);
            userOTPRepository.save(otp);
        } catch (Exception e) {
            log.error("Email sending error ", e);
            throw new EmailSendingException("Couldn't send OTP in email for account verification");
        }
    }

    @Transactional
    public ApiResponse verifyUser(String username, String otpCode) throws InvalidDataException {
        AuthUser.VerifyUserRequest verify = new AuthUser.VerifyUserRequest();
        verify.setUsername(username);
        verify.setOtpCode(otpCode);
        return verifyUser(verify);
    }

    @Transactional
    public ApiResponse verifyUser(AuthUser.VerifyUserRequest user) throws InvalidDataException {

        if(!redisRateLimitService.isVerifyUserAllowed(user.getUsername())) {
            throw new TooManyRequestException("Too many request, Please wait and try later");
        }

        User retrieveUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new InvalidDataException("No Registered User Found For Verification"));

        if (retrieveUser.isEnabled()) {
            return ApiResponse.builder()
                    .sucs(true)
                    .userDetail(retrieveUser.getUsername())
                    .message("User already verified").build();
        }

        UserOTP userOTP = userOTPRepository.findActiveOTPByUserIdAndCode(retrieveUser.getId(), user.getOtpCode(), OTPType.ACCOUNT_VERIFICATION.getName())
                .orElseThrow(() -> new InvalidDataException("Invalid OTP"));

        if (userOTP.getIsUsed()) {
            throw new InvalidDataException("OTP already used");
        }

        if (LocalDateTime.now().isAfter(userOTP.getExpiryTime())) {
            generateOTPForUserVerification(retrieveUser);
            throw new InvalidDataException("Expired OTP, New OTP Generated");
        }

        userOTP.setIsUsed(true);
        userOTP.setIsActive(false);
        userOTP.setValidatedTime(LocalDateTime.now());
        userOTP.setUpdatedDate(LocalDateTime.now());
        userOTPRepository.save(userOTP);

        retrieveUser.setEnabled(true);
        retrieveUser.setUpdatedDate(LocalDateTime.now());
        userRepository.save(retrieveUser);

        try {
            emailService.sendGenericEmailMessage(retrieveUser.getEmail(),
                    retrieveUser.getFullName(),
                    "Your account successfully verified",
                    EmailTemplateName.GENERIC_MESSAGE_MAIL,
                    "Account Verification"
            );
        } catch (Exception e) {
            log.error("Mail sending error", e);
        }

        return ApiResponse.builder()
                .sucs(true)
                .businessCode(ApiResponse.BusinessCode.OK.getValue())
                .userDetail(user.getUsername())
                .message("Successfully verified").build();
    }

    public Login.LoginResponse doLogin(Login.LoginRequest request) throws UserNotActiveException, UserAccountIsLocked {

        User usr = userRepository.findByUsernameOrEmail(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Wrong username or email"));

        if (!usr.isEnabled()) {
            throw new UserNotActiveException("User not activated yet, Please check email");
        }

        if (!usr.isAccountNonLocked()) {
            throw new UserAccountIsLocked("User account is locked");
        }

        String saltedPassword = request.getPassword() + usr.getSalt();

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        saltedPassword
                )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullName());
        claims.put("email", user.getEmail());

        var accessToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
        var refreshToken = jwtService.generateRefreshToken(claims, (User) auth.getPrincipal());
//         tokenService.revokeUserAllTokens(user, TokenType.ACCESS_TOKEN);
//        redisTokenService.deleteToken(user.getUsername(), TokenType.ACCESS_TOKEN);

        tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);
        redisTokenService.saveToken(TokenType.ACCESS_TOKEN, accessToken, user.getUsername(), jwtService.jwtExpiration);

        // tokenService.saveUserToken(user, accessToken, TokenType.ACCESS_TOKEN);
        tokenService.saveUserToken(user, refreshToken, TokenType.REFRESH_TOKEN);
        return Login.LoginResponse.builder().
                accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Login.LoginResponse refreshToken(Login.RefreshToken token) throws InvalidTokenException, UserNotActiveException, UserAccountIsLocked {
        String username = null;
        try {
            username = jwtService.extractUsername(token.getRefreshToken());
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new UserNotActiveException("User not activated yet, please check email");
        }

        if (!user.isAccountNonLocked()) {
            throw new UserAccountIsLocked("User account is locked");
        }

        boolean isTokenValid = tokenService.isTokenValid(token.getRefreshToken());

        if (jwtService.isTokenValid(token.getRefreshToken(), user) && isTokenValid) {
            var claims = new HashMap<String, Object>();
            claims.put("fullName", user.getFullName());
            claims.put("email", user.getEmail());

            var accessToken = jwtService.generateToken(claims, user);
            var refreshToken = jwtService.generateRefreshToken(claims, user);

            // tokenService.revokeUserAllTokens(user, TokenType.ACCESS_TOKEN);
//            redisTokenService.deleteToken(user.getUsername(), TokenType.ACCESS_TOKEN);

            tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);
            // tokenService.saveUserToken(user, accessToken, TokenType.ACCESS_TOKEN);
            redisTokenService.saveToken(TokenType.ACCESS_TOKEN, accessToken, user.getUsername(), jwtService.jwtExpiration);
            tokenService.saveUserToken(user, refreshToken, TokenType.REFRESH_TOKEN);
            return Login.LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        throw new InvalidTokenException("Invalid refresh token");
    }

}
