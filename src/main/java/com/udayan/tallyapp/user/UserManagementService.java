package com.udayan.tallyapp.user;

import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.common.PageResponse;
import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.redis.RedisTokenService;
import com.udayan.tallyapp.user.mapper.UserMapper;
import com.udayan.tallyapp.user.role.Role;
import com.udayan.tallyapp.user.role.RoleRepository;
import com.udayan.tallyapp.user.token.TokenService;
import com.udayan.tallyapp.user.token.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    RedisTokenService redisTokenService;

    public PageResponse<UserDTO.RegisteredUserResponse> getRegisteredUsers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<User> users;

        if (search.length() > 3) {
            users = userRepository.searchUsers(search, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        List<UserDTO.RegisteredUserResponse> regUsers = users.stream()
                .map(userMapper::toRegisteredUserResponse)
                .toList();
        return new PageResponse<>(
                regUsers,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );
    }

    public ApiResponse revokeToken(ControlUser.RevokeUserToken request) throws InvalidDataException {
        User user = userRepository.findByUsernameOrEmail(request.getUsername())
                .orElseThrow(() -> new InvalidDataException("No user found with the username"));
        tokenService.revokeUserAllTokens(user, TokenType.ACCESS_TOKEN);
        tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);
        redisTokenService.deleteToken(user.getUsername(), TokenType.ACCESS_TOKEN);
        return ApiResponse.builder()
                .sucs(true)
                .message("User all token revoked successfully")
                .userDetail(request.getUsername())
                .build();
    }

    public ApiResponse changeUserRole(ControlUser.ChangeUserRole request, User currentUser) throws InvalidDataException {

        if (request.getUsername().equals(currentUser.getUsername())) {
            throw new InvalidDataException("You can't changed your own role");
        }

        User user = userRepository.findByUsernameOrEmail(request.getUsername())
                .orElseThrow(() -> new InvalidDataException("No user found with the username"));

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new InvalidDataException("Unknown Role"));

        if (user.getRoles().stream().anyMatch(existingRole -> existingRole.getName().equals(request.getRole()))) {
            throw new InvalidDataException("The user already has this role");
        }

        List<Role> updatedRoles = new ArrayList<>(user.getRoles());
        updatedRoles.clear(); // If you want to replace the role completely
        updatedRoles.add(role);

        user.setRoles(updatedRoles);
        userRepository.save(user);

        tokenService.revokeUserAllTokens(user, TokenType.ACCESS_TOKEN);
        tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);

        return ApiResponse.builder()
                .sucs(true)
                .message("User role changed successfully and Token Invalidated")
                .userDetail(request.getUsername())
                .build();
    }

    public ApiResponse accountLock(ControlUser.LockUser request, User currentUser) throws InvalidDataException {

        if (request.getUsername().equals(currentUser.getUsername())) {
            throw new InvalidDataException("You can't lock your own account");
        }

        User user = userRepository.findByUsernameOrEmail(request.getUsername())
                .orElseThrow(() -> new InvalidDataException("No user found with the username"));

        user.setAccountLocked(request.getAccountLocked());

        userRepository.save(user);

        if (request.getAccountLocked()) {
            tokenService.revokeUserAllTokens(user, TokenType.ACCESS_TOKEN);
            tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);
            return ApiResponse.builder()
                    .sucs(true)
                    .message("User account is locked successfully and Token Invalidated")
                    .userDetail(request.getUsername())
                    .build();
        } else {
            return ApiResponse.builder()
                    .sucs(true)
                    .message("User account is unblocked successfully")
                    .userDetail(request.getUsername())
                    .build();

        }
    }
}
