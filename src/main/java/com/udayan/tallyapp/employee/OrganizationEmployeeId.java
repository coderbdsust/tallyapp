package com.udayan.tallyapp.employee;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationEmployeeId {
    @Column(name = "employees_id")
    private UUID employeesId;

    @Column(name = "organization_id")
    private UUID organizationId;
}
