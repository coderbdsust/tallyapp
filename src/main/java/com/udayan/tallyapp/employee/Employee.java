package com.udayan.tallyapp.employee;


import com.udayan.tallyapp.model.BaseEntity;
import com.udayan.tallyapp.organization.Organization;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity {

    private String fullName;
    private LocalDate dateOfBirth;
    private String mobileNo;
    private String empAddressLine;
    private String empCity;
    private String empPostcode;
    private String empCountry;
    @Enumerated(EnumType.STRING)
    private EmployeeType employeeType;
    @Enumerated(EnumType.STRING)
    private EmployeeBillingType employeeBillingType;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Double billingRate=0.0;
    private Double dailyAllowance=0.0;
    @ManyToMany(mappedBy = "employees")
    private List<Organization> organization;
}
