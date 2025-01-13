package com.udayan.tallyapp.user;

import com.udayan.tallyapp.user.address.AddressDTO;
import com.udayan.tallyapp.user.address.AddressService;
import com.udayan.tallyapp.customexp.InvalidDataException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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

    @PostMapping("/address/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addAddress(@Valid @RequestBody ArrayList<AddressDTO.AddressRequest> addressRequestList) throws InvalidDataException {
        log.debug("/address/create");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(addressService.saveAddressList(addressRequestList, currentUser));
    }

    @GetMapping("/address/list")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAddressList() throws InvalidDataException {
        log.debug("/address/list");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(addressService.getAddressResponseList(currentUser));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePassword.ChangeUserPasswordRequest changePassReq) throws InvalidDataException {
        log.debug("/users/v1/change-password");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.changePassword(changePassReq, currentUser));
    }

}
