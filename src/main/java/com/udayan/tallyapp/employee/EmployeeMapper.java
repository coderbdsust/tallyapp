package com.udayan.tallyapp.employee;

import com.udayan.tallyapp.fileuploader.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeMapper {


    @Autowired
    StorageService storageService;

    public Employee requestToEntity(EmployeeDTO.EmployeeRequest empRequest) {
        Employee employee = new Employee();
        employee.setId(empRequest.getId());
        employee.setFullName(empRequest.getFullName());
        employee.setDateOfBirth(empRequest.getDateOfBirth());
        employee.setMobileNo(empRequest.getMobileNo());
        employee.setProfileImage(empRequest.getProfileImage());
        employee.setEmpAddressLine(empRequest.getEmpAddressLine());
        employee.setEmpCity(empRequest.getEmpCity());
        employee.setEmpPostcode(empRequest.getEmpPostcode());
        employee.setEmpCountry(empRequest.getEmpCountry());
        employee.setEmployeeType(empRequest.getEmployeeType());
        employee.setStatus(empRequest.getStatus());
        employee.setEmployeeBillingType(empRequest.getEmployeeBillingType());
        employee.setBillingRate(empRequest.getBillingRate());
        employee.setDailyAllowance(empRequest.getDailyAllowance());
        return employee;
    }

    public EmployeeDTO.EmployeeResponse entityToResponse(Employee employee) {
        return EmployeeDTO.EmployeeResponse.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .dateOfBirth(employee.getDateOfBirth())
                .mobileNo(employee.getMobileNo())
                .profileImage(storageService.getFullURL(employee.getProfileImage()))
                .empAddressLine(employee.getEmpAddressLine())
                .empCity(employee.getEmpCity())
                .empPostcode(employee.getEmpPostcode())
                .empCountry(employee.getEmpCountry())
                .employeeType(employee.getEmployeeType())
                .status(employee.getStatus())
                .employeeBillingType(employee.getEmployeeBillingType())
                .billingRate(employee.getBillingRate())
                .dailyAllowance(employee.getDailyAllowance())
                .build();
    }
}
