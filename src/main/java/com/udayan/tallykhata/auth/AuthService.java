package com.udayan.tallykhata.auth;

import com.udayan.tallykhata.auth.exp.InvalidTokenException;
import com.udayan.tallykhata.common.ApiResponse;
import com.udayan.tallykhata.email.EmailService;
import com.udayan.tallykhata.email.EmailTemplateName;
import com.udayan.tallykhata.security.jwt.JwtService;
import com.udayan.tallykhata.user.User;
import com.udayan.tallykhata.user.UserRepository;
import com.udayan.tallykhata.user.exp.DuplicateKeyException;
import com.udayan.tallykhata.user.exp.UserAccountIsLocked;
import com.udayan.tallykhata.user.exp.UserNotActiveException;
import com.udayan.tallykhata.user.otp.OTPType;
import com.udayan.tallykhata.user.otp.UserOTP;
import com.udayan.tallykhata.user.otp.UserOTPRepository;
import com.udayan.tallykhata.user.role.Role;
import com.udayan.tallykhata.user.role.RoleRepository;
import com.udayan.tallykhata.user.token.TokenService;
import com.udayan.tallykhata.user.token.TokenType;
import com.udayan.tallykhata.utils.Utils;
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
import java.util.Optional;

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
    private  JwtService jwtService;

    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Value("${application.mailing.activation-url}")
    private String accountActivationURL;

    @Transactional
    public AuthUser.UserRequest registerUser(AuthUser.UserRequest userRequest) throws DuplicateKeyException {
        Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("Role 'USER' not initiated correctly"));

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setSalt(Utils.generateSalt(32));
        user.setPassword(passwordEncoder.encode(userRequest.getPassword() + user.getSalt()));
        user.setMobileNo(userRequest.getMobileNo());
        user.setFullName(userRequest.getFullName());
        user.setDateOfBirth(userRequest.getDateOfBirth());
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

        if (userRepository.findByMobileNo(userRequest.getMobileNo()).isPresent()) {
            throw new DuplicateKeyException("Mobile number already taken by user");
        }

        user = userRepository.save(user);
        userRequest.setId(user.getId());

        generateOTPForUserVerification(user);

        return userRequest;
    }

    private void generateOTPForUserVerification(User user) {
        UserOTP otp = new UserOTP();
        otp.setOtp(Utils.generateOTP(6));
        otp.setExpiryTime(LocalDateTime.now().plusDays(2));
        otp.setIsUsed(false);
        otp.setIsActive(true);
        otp.setOtpType(OTPType.ACCOUNT_VERIFICATION.getName());
        otp.setUser(user);
        UserOTP saveOtp = userOTPRepository.save(otp);
        log.debug("{}", saveOtp);

        sendAccountActivationEmail(user, otp);
    }

    private void sendAccountActivationEmail(User user, UserOTP otp) {
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
        }
    }

    @Transactional
    public ApiResponse verifyUser(String username, String otpCode) {
        AuthUser.VerifyUserRequest verify = new AuthUser.VerifyUserRequest();
        verify.setUsername(username);
        verify.setOtpCode(otpCode);
        return verifyUser(verify);
    }

    @Transactional
    public ApiResponse verifyUser(AuthUser.VerifyUserRequest user) {
        Optional<User> usrOptional = userRepository.findByUsername(user.getUsername());
        if (usrOptional.isEmpty()) {
            return ApiResponse.builder()
                    .sucs(false)
                    .userDetail(user.getUsername())
                    .message("No Registered User Found For Verification").build();
        }

        User retrieveUser = usrOptional.get();
        if (retrieveUser.isEnabled()) {
            return ApiResponse.builder()
                    .sucs(true)
                    .userDetail(retrieveUser.getUsername())
                    .message("User already verified").build();
        }

        Optional<UserOTP> otpOptional = userOTPRepository.findActiveOTPByUserIdAndCode(retrieveUser.getId(), user.getOtpCode(), OTPType.ACCOUNT_VERIFICATION.getName());

        if (otpOptional.isEmpty()) {
            return ApiResponse.builder()
                    .sucs(false)
                    .userDetail(user.getUsername())
                    .message("Invalid OTP").build();
        }

        UserOTP otp = otpOptional.get();

        if (otp.getIsUsed()) {
            return ApiResponse.builder()
                    .sucs(false)
                    .userDetail(user.getUsername())
                    .message("OTP already used").build();
        }

        if (LocalDateTime.now().isAfter(otp.getExpiryTime())) {
            generateOTPForUserVerification(retrieveUser);
            return ApiResponse.builder()
                    .sucs(false)
                    .userDetail(user.getUsername())
                    .message("Expired OTP, New OTP Generated").build();
        }

        otp.setIsUsed(true);
        otp.setIsActive(false);
        otp.setValidatedTime(LocalDateTime.now());
        otp.setUpdatedDate(LocalDateTime.now());
        userOTPRepository.save(otp);

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
                .userDetail(user.getUsername())
                .message("Successfully verified").build();
    }

    public Login.LoginResponse doLogin(Login.LoginRequest request) throws UserNotActiveException, UserAccountIsLocked {

        User usr = userRepository.findByUsernameOrEmail(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username or Email not found"));

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
        claims.put("email",user.getEmail());

        var accessToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
        var refreshToken = jwtService.generateRefreshToken(claims, (User) auth.getPrincipal());
        tokenService.revokeUserAllTokens(user, TokenType.ACCESS_TOKEN);
        tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);
        tokenService.saveUserToken(user, accessToken, TokenType.ACCESS_TOKEN);
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
            claims.put("email",user.getEmail());

            var accessToken = jwtService.generateToken(claims, user);
            var refreshToken = jwtService.generateRefreshToken(claims, user);

            tokenService.revokeUserAllTokens(user, TokenType.ACCESS_TOKEN);
            tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);
            tokenService.saveUserToken(user, accessToken, TokenType.ACCESS_TOKEN);
            tokenService.saveUserToken(user, refreshToken, TokenType.REFRESH_TOKEN);
            return Login.LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        throw new InvalidTokenException("Invalid refresh token");
    }

}
