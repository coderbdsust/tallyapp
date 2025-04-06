package com.udayan.tallyapp.organization;


import com.udayan.tallyapp.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/organization/v1")
@Slf4j
@RequiredArgsConstructor
public class OrganizationController {

    @Autowired
    OrganizationService organizationService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addOrganization(@Valid @RequestBody OrganizationDTO.OrganizationRequest orgRequest) {
        log.debug("/organization/v1/");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(organizationService.createOrganization(orgRequest, currentUser));
    }

    @PostMapping("/add-users-to-organization/{organizationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUsersToOrganization(@PathVariable("organizationId") UUID organizationId, @RequestBody List<UUID> userIds) {
        log.debug("/organization/v1/add-users-to-organization : {}",userIds);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(organizationService.addUsersToOrganization(organizationId, userIds, currentUser));
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getOrganizations() {
        log.debug("/organization/v1/list");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(organizationService.getOrganizations(currentUser));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getOrganization(@PathVariable UUID id) {
        log.debug("/organization/v1/{}",id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(organizationService.getOrganization(id, currentUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteOrganization(@PathVariable UUID id) {
        log.debug("/organization/v1/{}",id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(organizationService.deleteOrganization(id, currentUser));
    }

    @GetMapping("/{organizationId}/total-employee")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getOrganizationTotalEmployee(@PathVariable("organizationId") UUID orgId) {
        log.debug("/organization/v1/total-employee {}",orgId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(organizationService.getOrganizationTotalEmployee(orgId, currentUser));
    }

    @GetMapping("/{organizationId}/top-employee")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getOrganizationTopEmployee(@PathVariable("organizationId") UUID orgId) {
        log.debug("/organization/v1/top-employee {}",orgId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(organizationService.getOrganizationTopEmployee(orgId, currentUser));
    }
}
