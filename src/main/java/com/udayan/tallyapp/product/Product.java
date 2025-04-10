package com.udayan.tallyapp.product;

import com.udayan.tallyapp.employee.Employee;
import com.udayan.tallyapp.model.BaseEntity;
import com.udayan.tallyapp.organization.Organization;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "product")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {
    private String name;
    private String description;
    private Double employeeCost;
    private Double productionCost;
    private Double sellingPrice;
    private Double soldPrice;
    private String imageUrl;
    private Boolean sold;
    private LocalDate soldDate;
    @ManyToOne
    @JoinColumn(name = "made_by", referencedColumnName = "id")
    private Employee madeBy;
    @ManyToOne
    @JoinColumn(name = "owner_organization", referencedColumnName = "id")
    private Organization ownerOrganization;
}
