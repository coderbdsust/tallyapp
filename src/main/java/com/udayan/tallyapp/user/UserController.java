package com.udayan.tallyapp.user;

import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.user.address.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/v1")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserProfile() throws InvalidDataException {
        log.debug("/users/v1/profile");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUserProfile(currentUser.getUsername()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UserDTO.UserRequest userRequest) throws InvalidDataException {
        log.debug("/users/v1/profile {}", userRequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateUserProfile(userRequest, currentUser.getUsername()));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePassword.ChangeUserPasswordRequest changePassReq) throws InvalidDataException {
        log.debug("/users/v1/change-password");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.changePassword(changePassReq, currentUser));
    }

    @GetMapping("/search-users-for-organization")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchUsers(@RequestParam(name = "searchKey", defaultValue = "", required = false) String searchKey,
                                         @RequestParam(name = "page", defaultValue = "0", required = false) int page,
                                         @RequestParam(name = "size", defaultValue = "10", required = false) int size){
        log.debug("/users/v1/search-users-for-organization : {}, {}, {}", searchKey, page, size);
        return ResponseEntity.ok(userService.searchUsers(searchKey, page, size));
    }
}
