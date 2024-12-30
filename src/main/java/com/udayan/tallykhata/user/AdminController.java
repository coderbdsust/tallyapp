package com.udayan.tallykhata.user;

import com.udayan.tallykhata.user.address.AddressService;
import com.udayan.tallykhata.user.exp.InvalidDataException;
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
@RequestMapping("/users/admin/v1")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AddressService addressService;

    @GetMapping("/registered/list")
    public ResponseEntity<?> getRegisteredUsers(@RequestParam(name = "page", defaultValue = "0", required = false) int page, @RequestParam(name = "size", defaultValue = "10", required = false) int size){
        log.debug("/users/admin/v1/registered/list - page {}, size {} ", page, size);
        return ResponseEntity.ok(adminService.getRegisteredUsers(page, size));
    }

    @PostMapping("/revoke/token")
    public ResponseEntity<?> revokeUserToken(@Valid @RequestBody ControlUser.revokeUserToken request) throws InvalidDataException {
        log.debug("/users/admin/v1/revoke/token - {} ", request);
        return ResponseEntity.ok(adminService.revokeToken(request));
    }

    @PostMapping("/change/role")
    public ResponseEntity<?> changeUserRole(@Valid @RequestBody ControlUser.changeUserRole request) throws InvalidDataException {
        log.debug("/users/admin/v1/change/role - {} ", request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(adminService.changeUserRole(request, currentUser));
    }

    @PostMapping("/account/lock")
    public ResponseEntity<?> accountLock(@Valid @RequestBody ControlUser.LockUser request) throws InvalidDataException {
        log.debug("/users/admin/v1/account/lock - {} ", request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(adminService.accountLock(request, currentUser));
    }
}
