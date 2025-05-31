package com.udayan.tallyapp.employee;


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

import java.util.UUID;

@RestController
@RequestMapping("/employee/v1")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @PostMapping("{organizationId}/add")
    public ResponseEntity<?> addEmployee(@PathVariable("organizationId") UUID organizationId, @Valid @RequestBody EmployeeDTO.EmployeeRequest empRequest) {
        log.debug("/employee/v1/{}/add {}",organizationId, empRequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(employeeService.addEmployee(organizationId, empRequest, currentUser));
    }

    @GetMapping("{organizationId}/all-employee")
    public ResponseEntity<?> allEmployees(@PathVariable("organizationId") UUID organizationId,
                                          @RequestParam(name = "page", defaultValue = "0", required = false) int page,
                                          @RequestParam(name = "size", defaultValue = "10", required = false) int size,
                                          @RequestParam(name = "search", defaultValue = "", required = false) String search) {
        log.debug("/employee/v1/all-employee {}", organizationId);
        return ResponseEntity.ok(employeeService.allEmployeeByOrganization(organizationId, page, size, search));
    }

    @GetMapping("/employee-type")
    public ResponseEntity<?> employeeType() {
        log.debug("/employee/v1/employee-type");
        return ResponseEntity.ok(employeeService.getEmployeeType());
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable UUID employeeId) {
        log.debug("/employee/v1/employeeId : {}", employeeId);
        return ResponseEntity.ok(employeeService.deleteEmployee(employeeId));
    }


    @GetMapping("/employee-status-list")
    public ResponseEntity<?> employeeStatusList() {
        log.debug("/employee/v1/employee-status-list");
        return ResponseEntity.ok(employeeService.getEmployeeStatusList());
    }

    @GetMapping("/employee-billing-type")
    public ResponseEntity<?> employeeBillingType() {
        log.debug("/employee/v1/employee-billing-type");
        return ResponseEntity.ok(employeeService.getEmployeeBillingType());
    }


}
