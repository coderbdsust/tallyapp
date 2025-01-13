package com.udayan.tallyapp.user;


import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.user.mapper.UserMapper;
import com.udayan.tallyapp.user.token.TokenService;
import com.udayan.tallyapp.user.token.TokenType;
import com.udayan.tallyapp.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenService tokenService;

    public RegisteredUserResponse getUserProfile(String username) throws InvalidDataException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidDataException("No data found using this username : " + username));
        return userMapper.toRegisteredUserResponse(user);
    }

    public ApiResponse changePassword(ChangePassword.ChangeUserPasswordRequest request, User user) throws InvalidDataException {

        String oldPassword = request.getOldPassword()+user.getSalt();
        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidDataException("Old password is wrong");
        }

        if(request.getOldPassword().equals(request.getPassword())){
            throw new InvalidDataException("You used this password recently. Please choose a different one.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Password and Confirmed Password isn't same");
        }

        user.setSalt(Utils.generateSalt(32));
        user.setPassword(passwordEncoder.encode(request.getPassword() + user.getSalt()));
        user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);
        log.debug("User password changed successfully : {}",user.getUsername());
        tokenService.revokeUserAllTokens(user, TokenType.ACCESS_TOKEN);
        tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);
        log.debug("User all token revoked : {}",user.getUsername());

        return ApiResponse.builder()
                .sucs(true)
                .userDetail(user.getUsername())
                .message("User password changed successfully").build();
    }
}
