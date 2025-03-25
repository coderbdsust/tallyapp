package com.udayan.tallyapp.employee;


import com.udayan.tallyapp.organization.Organization;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organization_employees")
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationEmployee {

    @EmbeddedId
    private OrganizationEmployeeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false, insertable = false, updatable = false)
    private Organization organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employees_id", nullable = false, insertable = false, updatable = false)
    private Employee employeesId;
}
