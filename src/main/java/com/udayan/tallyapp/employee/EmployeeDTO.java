package com.udayan.tallyapp.employee;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.udayan.tallyapp.user.deserializer.DatePatternDeserializer;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

public class EmployeeDTO {

    @Data
    @Builder
    public static class EmployeeRequest {
        private UUID id;
        @NotEmpty(message = "Employee name can't be empty")
        private String fullName;
        @Past(message = "Employee date of birth must be from past")
        @JsonDeserialize(using= DatePatternDeserializer.class )
        private LocalDate dateOfBirth;
        @NotEmpty(message = "Employee mobile no can't be empty")
        @Pattern(regexp = "01[3-9]\\d{8}$",message = "Employee mobile number is invalid")
        private String mobileNo;
        private String profileImage;
        private String empAddressLine;
        private String empCity;
        private String empPostcode;
        private String empCountry;
        @Enumerated(EnumType.STRING)
        @NotNull(message = "Employee type can't be empty")
        private EmployeeType employeeType;
        @Enumerated(EnumType.STRING)
        @NotNull(message = "Employee status can't be empty")
        private Status status;
        @Enumerated(EnumType.STRING)
        @NotNull(message = "Employee billing type can't be empty")
        private EmployeeBillingType employeeBillingType;
        private Double billingRate = 0.0;
        private Double dailyAllowance=0.0;
    }

    @Data
    @Builder
    public static class EmployeeResponse {
        private UUID id;
        private String fullName;
        private LocalDate dateOfBirth;
        private String mobileNo;
        private String profileImage;
        private String empAddressLine;
        private String empCity;
        private String empPostcode;
        private String empCountry;
        private EmployeeType employeeType;
        private Status status;
        private EmployeeBillingType employeeBillingType;
        private Double billingRate = 0.0;
        private Double dailyAllowance=0.0;
    }
}
