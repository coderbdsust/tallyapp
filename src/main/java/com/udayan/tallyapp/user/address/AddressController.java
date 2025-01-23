package com.udayan.tallyapp.user.address;

import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.user.User;
import com.udayan.tallyapp.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/users/v1")
@Slf4j
@RequiredArgsConstructor
public class AddressController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @PostMapping("/address/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addAddress(@Valid @RequestBody AddressDTO.AddressRequest addressRequest) throws InvalidDataException {
        log.debug("/address/create");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return new ResponseEntity<>(addressService.saveOrUpdateAddress(addressRequest, currentUser), HttpStatus.OK);
    }

    @PostMapping("/address/add-list")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addAddress(@Valid @RequestBody ArrayList<AddressDTO.AddressRequest> addressRequestList) throws InvalidDataException {
        log.debug("/address/add");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return new ResponseEntity<>(addressService.saveAddressList(addressRequestList, currentUser), HttpStatus.OK);
    }

    @GetMapping("/address/list")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAddressList() throws InvalidDataException {
        log.debug("/address/list");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(addressService.getAddressResponseList(currentUser));
    }

    @DeleteMapping("/address/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteAddress(@PathVariable("id") UUID id) throws InvalidDataException {
        log.debug("/address/{}",id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(addressService.deleteAddress(id, currentUser));
    }
}
