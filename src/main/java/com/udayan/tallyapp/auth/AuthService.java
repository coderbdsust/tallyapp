package com.udayan.tallyapp.auth;

import com.udayan.tallyapp.auth.exp.ValidTFAVerificationChannelNotFoundException;
import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.customexp.*;
import com.udayan.tallyapp.email.EmailService;
import com.udayan.tallyapp.email.EmailTemplateName;
import com.udayan.tallyapp.redis.RedisRateLimitService;
import com.udayan.tallyapp.redis.RedisTokenService;
import com.udayan.tallyapp.redis.exp.TooManyRequestException;
import com.udayan.tallyapp.security.jwt.JwtService;
import com.udayan.tallyapp.user.GenderType;
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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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

    @Value("${application.security.jwt.expiration}")
    private long accessTokenExpiry;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiry;

    @Value("${application.security.web.origin}")
    private String applicationSecurityWebOrigin;

    @Value("${application.security.login.otp.expiration.minute}")
    private long applicationLoginOTPExpiryMinute;

    @Transactional
    public AuthUser.UserRequest initiateAdmin(AuthUser.UserRequest userRequest) {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow(() -> new IllegalStateException("Role 'ADMIN' not initiated correctly"));

        Optional<User> adminByEmail = userRepository.findByEmail(userRequest.getEmail());
        if(adminByEmail.isPresent()){
            userRequest.setId(adminByEmail.get().getId());
            return userRequest;
        }

        Optional<User> adminByUsername = userRepository.findByUsername(userRequest.getUsername());
        if(adminByUsername.isPresent()){
            userRequest.setId(adminByUsername.get().getId());
            return userRequest;
        }
        Optional<User> adminByMobileNo = userRepository.findByEmail(userRequest.getMobileNo());
        if(adminByMobileNo.isPresent()){
            userRequest.setId(adminByMobileNo.get().getId());
            return userRequest;
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setSalt(Utils.generateSalt(32));
        user.setPassword(passwordEncoder.encode(userRequest.getPassword() + user.getSalt()));
        user.setMobileNo(userRequest.getMobileNo());
        user.setFullName(userRequest.getFullName());
        user.setDateOfBirth(userRequest.getDateOfBirth());
        user.setGender(userRequest.getGender());
        user.setRoles(List.of(adminRole));
        user.setEnabled(true);
        user.setTfaEnabled(false);
        user.setIsMobileNumberVerified(true);

        user = userRepository.save(user);

        userRequest.setId(user.getId());

        return userRequest;
    }

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

        UserOTP otp = generateOTPForUserVerification(user, accountVerificationOTPExpirationMinute, OTPType.ACCOUNT_VERIFICATION);
        sendEmail(user, otp, EmailTemplateName.ACTIVATE_ACCOUNT, "Account Verification");
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

        UserOTP otp = generateOTPForUserVerification(user, accountVerificationOTPExpirationMinute, OTPType.ACCOUNT_VERIFICATION);

        sendEmail(user, otp, EmailTemplateName.ACTIVATE_ACCOUNT, "Account Verification");

        return ApiResponse.builder()
                .sucs(true)
                .message("New OTP generated, Please check email")
                .businessCode(ApiResponse.BusinessCode.USER_NOT_VERIFIED.getValue())
                .userDetail(user.getEmail())
                .build();
    }

    private UserOTP generateOTPForUserVerification(User user,  long expiryInMinute, OTPType otpType) {
        UserOTP otp = new UserOTP();
        otp.setOtp(Utils.generateOTP(6));
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(expiryInMinute));
        otp.setIsUsed(false);
        otp.setIsActive(true);
        otp.setOtpType(otpType.getName());
        otp.setUser(user);
        UserOTP saveOtp = userOTPRepository.save(otp);
        log.debug("{}", saveOtp);
        return saveOtp;
    }

    private void sendEmail(User user, UserOTP otp, EmailTemplateName templateName, String subject) throws EmailSendingException {
        try {
            this.emailService.sendEmailForActivateAccount(
                    user.getEmail(),
                    user.getUsername(),
                    user.getFullName(),
                    templateName,
                    accountActivationURL,
                    otp.getOtp(),
                    subject
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

        UserOTP userOTP = userOTPRepository.findActiveOTPByUserParamAndCode(user.getUsername(), user.getOtpCode(), OTPType.ACCOUNT_VERIFICATION.getName())
                .orElseThrow(() -> new InvalidDataException("Invalid OTP"));

        if (userOTP.getIsUsed()) {
            throw new InvalidDataException("OTP already used");
        }

        if (LocalDateTime.now().isAfter(userOTP.getExpiryTime())) {
            throw new InvalidDataException("OTP already expired");
        }

        userOTP.setIsUsed(true);
        userOTP.setIsActive(false);
        userOTP.setValidatedTime(LocalDateTime.now());
        userOTPRepository.save(userOTP);

        User retrieveUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new InvalidDataException("No Registered User Found For Verification"));

        retrieveUser.setEnabled(true);
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

    public Object doLogin(Login.LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws UserNotActiveException, UserAccountIsLocked {

        User user = userRepository.findByUsernameOrEmail(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Wrong username or email"));

        validateUserStatus(user);

        String saltedPassword = request.getPassword() + user.getSalt();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), saltedPassword));

        if (Boolean.TRUE.equals(user.getTfaEnabled())) {
            log.debug("TFA is enabled for user: {}", user.getUsername());

            boolean useMobile = Boolean.TRUE.equals(user.getTfaByMobile());
            boolean useEmail = Boolean.TRUE.equals(user.getTfaByEmail());

            if (!useMobile && !useEmail) {
                throw new ValidTFAVerificationChannelNotFoundException("No valid TFA channel found for sending OTP");
            }

            UserOTP otp = generateOTPForUserVerification(user, applicationLoginOTPExpiryMinute, OTPType.ACCOUNT_LOGIN);
            StringBuilder otpChannels = new StringBuilder();

            if (useMobile) {
                otpChannels.append("mobile");
                // TODO: Implement SMS sending logic here
            }

            if (useEmail) {
                if (otpChannels.length() > 0) otpChannels.append(" and ");
                otpChannels.append("email");
                sendEmail(user, otp, EmailTemplateName.TFA_LOGIN_OTP, "Account Login OTP");
            }

            return Login.TwoFaRequiredResponse.builder()
                    .status(Login.LoginStatus.TFA_REQUIRED)
                    .username(user.getUsername())
                    .otpChannel(otpChannels.toString())
                    .otpTxnId(otp.getId().toString())
                    .message("OTP has been sent to your verified " + otpChannels)
                    .build();
        }

        return issueTokens(user, httpRequest, httpResponse);
    }


    private Object issueTokens(User  user, HttpServletRequest httpRequest, HttpServletResponse httpResponse){
        var claims = createClaims(user);
        String accessToken = jwtService.generateToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(claims, user);

        handleTokens(user, accessToken, refreshToken);
        boolean isBrowser = isBrowserRequest(httpRequest, Arrays.asList(applicationSecurityWebOrigin.split(",")));

        if (isBrowser) {
            log.debug("Detected browser-based login, setting cookies.");

            addCookie(httpResponse, "X-Tally-Access-Token", accessToken, accessTokenExpiry, "/", true, true, "Strict");
            addCookie(httpResponse, "X-Tally-Refresh-Token", refreshToken, refreshTokenExpiry, "/", true, true, "Strict");

            return Login.UserResponse.builder()
                    .status(Login.LoginStatus.SUCCESS)
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .role(user.getRoles().get(0).getName())
                    .accessTokenExpiry(jwtService.getAccessTokenExpiration())
                    .refreshTokenExpiry(jwtService.getRefreshTokenExpiration())
                    .build();
        } else {
            log.debug("Detected mobile/client login, returning tokens in body.");
            return Login.LoginResponse.builder()
                    .status(Login.LoginStatus.SUCCESS)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }

    private void addCookie(HttpServletResponse response, String name, String value, long maxAgeMS,
                           String path, boolean httpOnly, boolean secure, String sameSite) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(secure)
                .path(path)
                .maxAge(Duration.ofMillis(maxAgeMS))
                .sameSite(sameSite)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Validates if the user is active and not locked.
     */
    private void validateUserStatus(User user) throws UserNotActiveException, UserAccountIsLocked {
        if (!user.isEnabled()) {
            throw new UserNotActiveException("User not activated yet, Please check email");
        }
        if (!user.isAccountNonLocked()) {
            throw new UserAccountIsLocked("User account is locked");
        }
    }

    /**
     * Creates claims for JWT token.
     */
    private Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("fullName", user.getFullName());
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRoles().get(0).getName());
        return claims;
    }

    /**
     * Handles token revocation and saving new tokens.
     */
    private void handleTokens(User user, String accessToken, String refreshToken) {
        tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);
        tokenService.saveUserToken(user, refreshToken, TokenType.REFRESH_TOKEN);
        redisTokenService.saveToken(user.getUsername(), TokenType.ACCESS_TOKEN, accessToken, jwtService.jwtExpiration);
    }


    public Object refreshToken(HttpServletRequest request, HttpServletResponse response) throws InvalidTokenException, UserNotActiveException, UserAccountIsLocked {
       String refreshToken;
       boolean isBrowser = isBrowserRequest(request, Arrays.asList(applicationSecurityWebOrigin.split(",")));
        if (isBrowser) {
            // Web client — read from cookie
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                throw new InvalidTokenException("Missing refresh token cookie");
            }
            refreshToken = Arrays.stream(cookies)
                    .filter(c -> "X-Tally-Refresh-Token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (refreshToken == null) {
                throw new InvalidTokenException("Missing refresh token cookie");
            }
        } else {
            // Get token from Authorization header (e.g., Bearer <token>)
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new InvalidTokenException("Invalid refresh token");
            }
            refreshToken = authHeader.substring(7);
        }

        String username = null;
        try {
            username = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        validateUserStatus(user);

        boolean isTokenValid = tokenService.isTokenValid(refreshToken);

        if (jwtService.isTokenValid(refreshToken, user) && isTokenValid) {
            var claims = createClaims(user);

            var newAccessToken = jwtService.generateToken(claims, user);
            var newRefreshToken = jwtService.generateRefreshToken(claims, user);

            tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);
            tokenService.saveUserToken(user, newRefreshToken, TokenType.REFRESH_TOKEN);
            redisTokenService.saveToken(user.getUsername(), TokenType.ACCESS_TOKEN, newAccessToken, accessTokenExpiry);

            if (isBrowser) {
                log.debug("Detected browser-based login, setting cookies.");
                addCookie(response, "X-Tally-Access-Token", newAccessToken, accessTokenExpiry, "/", true, true, "Strict");
                addCookie(response, "X-Tally-Refresh-Token", newRefreshToken, refreshTokenExpiry, "/", true, true, "Strict");
                return Login.UserResponse.builder()
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .role(user.getRoles().get(0).getName())
                        .accessTokenExpiry(jwtService.getAccessTokenExpiration())
                        .refreshTokenExpiry(jwtService.getRefreshTokenExpiration())
                        .build();
            } else {
                log.debug("Detected mobile/client login, returning tokens in body.");
                return Login.LoginResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();
            }
        }
        throw new InvalidTokenException("Invalid refresh token");
    }

    public boolean isBrowserRequest(HttpServletRequest request, List<String> allowedOrigins) {
        String userAgent = request.getHeader("User-Agent");
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        log.debug("userAgent: {}",userAgent);
        log.debug("origin: {}",origin);
        log.debug("referer: {}",referer);

        boolean fromUserAgent = userAgent != null && userAgent.toLowerCase().contains("mozilla");
        boolean fromAllowedOrigin = origin != null && allowedOrigins.stream().anyMatch(origin::contains);
        boolean fromAllowedReferer = referer != null && allowedOrigins.stream().anyMatch(referer::contains);

        return fromUserAgent || fromAllowedOrigin || fromAllowedReferer;
    }

    public boolean isBrowserRequest(HttpServletRequest request){
        return this.isBrowserRequest(request, Arrays.asList(applicationSecurityWebOrigin.split(",")));
    }


    public List<GenderType> getGenderList() {
        return new ArrayList<>(Arrays.stream(GenderType.values()).toList());
    }

    public Object verifyLoginOtp(Login.@Valid OtpVerificationRequest otpRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws InvalidTokenException {

        if(!redisRateLimitService.haveAccountLoginOTPVerificationLimit(otpRequest.getUsername())) {
            throw new TooManyRequestException("Too many request, Please wait and try later");
        }

        User user  = userRepository.findByUsernameOrEmail(otpRequest.getUsername())
                .orElseThrow(()->new UsernameNotFoundException("User not found"));

        UserOTP userOTP = userOTPRepository.findByIdAndOtp(otpRequest.getOtpTxnId(), otpRequest.getOtp())
                .orElseThrow(()->new InvalidTokenException("Invalid OTP"));

        if (userOTP.getIsUsed() || !userOTP.getIsActive()) {
            throw new InvalidTokenException("Invalid OTP");
        }

        if (LocalDateTime.now().isAfter(userOTP.getExpiryTime())) {
            throw new InvalidTokenException("OTP already expired");
        }

        userOTP.setIsUsed(true);
        userOTP.setIsActive(false);
        userOTP.setValidatedTime(LocalDateTime.now());
        userOTPRepository.save(userOTP);

        return issueTokens(user, httpRequest, httpResponse);
    }

    public Object resendLoginOtp(Login.@Valid ResendLoginOtpRequest resendLoginOtpRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        throw  new InvalidDataException("Resend OTP request not implemented!");
    }
}
