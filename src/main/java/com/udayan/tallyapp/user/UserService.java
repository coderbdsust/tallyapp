package com.udayan.tallyapp.user;


import com.udayan.tallyapp.auth.TFAProvider;
import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.customexp.DuplicateKeyException;
import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.redis.RedisTokenService;
import com.udayan.tallyapp.user.authenticator.AuthenticatorAppService;
import com.udayan.tallyapp.user.mapper.UserMapper;
import com.udayan.tallyapp.user.shortprofile.ShortProfile;
import com.udayan.tallyapp.user.shortprofile.ShortProfileDTO;
import com.udayan.tallyapp.user.shortprofile.ShortProfileRepository;
import com.udayan.tallyapp.user.token.TokenService;
import com.udayan.tallyapp.user.token.TokenType;
import com.udayan.tallyapp.utils.Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ShortProfileRepository shortProfileRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenService tokenService;

    @Autowired
    RedisTokenService redisTokenService;

    @Autowired
    AuthenticatorAppService authenticatorAppService;

    public UserDTO.RegisteredUserResponse getUserProfile(String username) throws InvalidDataException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidDataException("No data found using this username : " + username));
        return userMapper.toRegisteredUserResponse(user);
    }

    public ApiResponse changePassword(ChangePassword.ChangeUserPasswordRequest request, User user) throws InvalidDataException {

        String oldPassword = request.getOldPassword() + user.getSalt();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidDataException("Old password is wrong");
        }

        if (request.getOldPassword().equals(request.getPassword())) {
            throw new InvalidDataException("You used this password recently. Please choose a different one.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Password and Confirmed Password isn't same");
        }

        user.setSalt(Utils.generateSalt(32));
        user.setPassword(passwordEncoder.encode(request.getPassword() + user.getSalt()));
        //user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);
        log.debug("User password changed successfully : {}", user.getUsername());
        //tokenService.revokeUserAllTokens(user, TokenType.ACCESS_TOKEN);
        redisTokenService.deleteToken(user.getUsername(), TokenType.ACCESS_TOKEN);
        tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);

        log.debug("User all token revoked : {}", user.getUsername());

        return ApiResponse.builder()
                .sucs(true)
                .userDetail(user.getUsername())
                .message("User password changed successfully").build();
    }

    @Transactional
    public ShortProfileDTO.ShortProfileResponse addShortProfile(ShortProfileDTO.ShortProfileRequest request, User user) {
        ShortProfile shortProfile = new ShortProfile();
        shortProfile.setId(request.getId());
        shortProfile.setDesignation(request.getDesignation());
        shortProfile.setSkills(request.getSkills());
        shortProfile.setCompanyName(request.getCompanyName());
        shortProfile.setUser(user);
        ShortProfile savedProfile = shortProfileRepository.save(shortProfile);
        return userMapper.shortProfileToResponse(savedProfile);
    }

    @Transactional
    public ArrayList<ShortProfileDTO.ShortProfileResponse> addShortProfile(ArrayList<ShortProfileDTO.ShortProfileRequest> requests, User user) {
        ArrayList<ShortProfileDTO.ShortProfileResponse> responses = new ArrayList<>();
        for (ShortProfileDTO.ShortProfileRequest request : requests) {
            ShortProfile shortProfile = new ShortProfile();
            shortProfile.setId(request.getId());
            shortProfile.setDesignation(request.getDesignation());
            shortProfile.setSkills(request.getSkills());
            shortProfile.setCompanyName(request.getCompanyName());
            shortProfile.setUser(user);
            ShortProfile savedProfile = shortProfileRepository.save(shortProfile);
            ShortProfileDTO.ShortProfileResponse response = userMapper.shortProfileToResponse(savedProfile);
            responses.add(response);
        }
        return responses;
    }

    public UserDTO.RegisteredUserResponse updateUserProfile(UserDTO.UserRequest userRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidDataException("No data found using this username : " + username));

        if (user.getMobileNo() != null &&
                !user.getMobileNo().equals(userRequest.getMobileNo())
                && userRepository.findByMobileNo(userRequest.getMobileNo()).isPresent()) {
            throw new DuplicateKeyException("Mobile number is used by other user");
        }

        user.setFullName(userRequest.getFullName());
        user.setGender(userRequest.getGender());
        user.setDateOfBirth(userRequest.getDateOfBirth());
        user.setMobileNo(userRequest.getMobileNo());
        // user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);
        return getUserProfile(username);
    }

    public ApiResponse deleteShortProfile(@Valid UUID id, User currentUser) {
        shortProfileRepository.deleteById(id);
        return ApiResponse
                .builder()
                .sucs(true)
                .businessCode(ApiResponse.BusinessCode.OK.getValue())
                .message("Short profile deleted successfully")
                .build();
    }

    public List<UserDTO.UserForOrgResponse> searchUsers(String searchKey, int page, int size) {
        if (searchKey.length() < 5) {
            throw new InvalidDataException("Minimum search key length is 5");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.searchUserByUsernameOrEmail(searchKey, pageable);
        return users.stream().map(u -> UserDTO.UserForOrgResponse
                        .builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .build())
                .toList();
    }

    public ApiResponse changeTFAByMobile(UserDTO.TFARequest tfaRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidDataException("User not found"));

        if (!user.getIsMobileNumberVerified()) {
            return buildApiResponse(
                    false,
                    ApiResponse.BusinessCode.MOBILE_NOT_VERIFIED,
                    user.getUsername(),
                    "Please verify your mobile number first"
            );
        }

        boolean enableTfa = Boolean.TRUE.equals(tfaRequest.getTfaEnable());

        if (enableTfa) {
            return enableMobileTFA(user);
        } else {
            return disableMobileTFA(user);
        }
    }

    private ApiResponse enableMobileTFA(User user) {
        user.enableTfaChannel(TFAProvider.Mobile);
        userRepository.save(user);

        return buildApiResponse(
                true,
                ApiResponse.BusinessCode.OK,
                user.getUsername(),
                "Two factor authentication is enabled using mobile"
        );
    }

    private ApiResponse disableMobileTFA(User user) {
        user.disableTfaChannel(TFAProvider.Mobile);
        userRepository.save(user);

        String message = user.isTfaEnabled()
                ? "Two factor authentication is disabled using mobile"
                : "Two factor authentication is disabled";

        return buildApiResponse(
                false,
                ApiResponse.BusinessCode.OK,
                user.getUsername(),
                message
        );
    }


    public ApiResponse changeTFAByEmail(UserDTO.TFARequest tfaRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidDataException("User not found"));
        boolean enableTfa = Boolean.TRUE.equals(tfaRequest.getTfaEnable());
        if (enableTfa) {
            user.enableTfaChannel(TFAProvider.Email);
            userRepository.save(user);
            String message = "Two factor authentication is enabled using email";
            return buildApiResponse(true, ApiResponse.BusinessCode.OK, user.getUsername(), message);
        }
        user.disableTfaChannel(TFAProvider.Email);
        userRepository.save(user);
        String msg = user.isTfaEnabled() ? "Two factor authentication is disabled using email" : "Two factor authentication is disabled";
        return buildApiResponse(false, ApiResponse.BusinessCode.OK, user.getUsername(), msg);
    }

    private ApiResponse buildApiResponse(Boolean sucs, ApiResponse.BusinessCode code, String username, String message) {
        return ApiResponse.builder()
                .sucs(sucs)
                .userDetail(username)
                .businessCode(code.getValue())
                .message(message)
                .build();
    }


    public UserDTO.TFAResponse checkTFAStatus(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidDataException("User not found"));

        return UserDTO.TFAResponse.builder()
                .byEmail(user.isTfaChannelEnabled(TFAProvider.Email))
                .byMobile(user.isTfaChannelEnabled(TFAProvider.Mobile))
                .byAuthenticator(user.isTfaChannelEnabled(TFAProvider.Authenticator))
                .build();
    }


}
