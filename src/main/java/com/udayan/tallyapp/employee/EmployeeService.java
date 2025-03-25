package com.udayan.tallyapp.employee;

import com.udayan.tallyapp.common.ApiResponse;
import com.udayan.tallyapp.common.PageResponse;
import com.udayan.tallyapp.customexp.DependencyException;
import com.udayan.tallyapp.customexp.InvalidDataException;
import com.udayan.tallyapp.organization.Organization;
import com.udayan.tallyapp.organization.OrganizationRepository;
import com.udayan.tallyapp.user.User;
import io.jsonwebtoken.lang.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    OrganizationEmployeeRepository organizationEmployeeRepository;

    @Autowired
    EmployeeMapper employeeMapper;

    public EmployeeDTO.EmployeeResponse addEmployee(UUID organizationId, EmployeeDTO.EmployeeRequest empRequest, User currentUser) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new InvalidDataException("No organization found using this id: " + organizationId));

        if (empRequest.getBillingRate() == null || empRequest.getBillingRate() <= 0.0) {
            throw new InvalidDataException("Billing rate can't be empty or zero (0.0)");
        }

        Employee employee = employeeMapper.requestToEntity(empRequest);
        employee = employeeRepository.save(employee);
        UUID employeeId = employee.getId();

        boolean employeeExists = organization.getEmployees().stream()
                .anyMatch(emp -> emp.getId().equals(employeeId));

        if (!employeeExists) {
            organization.getEmployees().add(employee);
            organizationRepository.save(organization);
        }

        return employeeMapper.entityToResponse(employee);
    }


    public ArrayList<EmployeeType> getEmployeeType() {
        return new ArrayList<>(Arrays.asList(EmployeeType.values()));
    }

    public PageResponse<EmployeeDTO.EmployeeResponse> allEmployeeByOrganization(UUID organizationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Employee> employees = employeeRepository.findAllByOrganizationId(organizationId, pageable);

        List<EmployeeDTO.EmployeeResponse> employeeList = employees
                .stream()
                .map(emp -> employeeMapper.entityToResponse(emp))
                .toList();

        return new PageResponse<>(
                employeeList,
                employees.getNumber(),
                employees.getSize(),
                employees.getTotalElements(),
                employees.getTotalPages(),
                employees.isFirst(),
                employees.isLast()
        );

    }

    public ArrayList<EmployeeBillingType> getEmployeeBillingType() {
        return new ArrayList<>(Arrays.asList(EmployeeBillingType.values()));
    }

    public ArrayList<Status> getEmployeeStatusList() {
        return new ArrayList<>(Arrays.asList(Status.values()));
    }

    @Transactional
    public ApiResponse deleteEmployee(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new InvalidDataException("No employee found")
        );
        try {

            organizationEmployeeRepository.deleteEmployeeByEmployeeId(employee);

            employeeRepository.delete(employee);

            return ApiResponse.builder()
                    .businessCode(ApiResponse.BusinessCode.OK.getValue())
                    .sucs(true)
                    .message("Employee successfully deleted")
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new DependencyException("Couldn't delete employee due to dependency");
        }
    }
}
