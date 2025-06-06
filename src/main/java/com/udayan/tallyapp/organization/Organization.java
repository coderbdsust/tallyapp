package com.udayan.tallyapp.organization;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.udayan.tallyapp.employee.Employee;
import com.udayan.tallyapp.employee.Status;
import com.udayan.tallyapp.model.BaseEntity;
import com.udayan.tallyapp.product.Product;
import com.udayan.tallyapp.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="organization")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends BaseEntity {

    private String orgName;
    private String orgRegNumber;
    private String orgTinNumber;
    private String orgVatNumber;
    private String orgMobileNo;
    private String orgEmail;
    private String orgOpenAt;
    private String orgOpenInWeek;
    private String orgOpeningTitle;
    private LocalDate since;
    private String orgAddressLine;
    private String orgAddressCity;
    private String orgAddressPostcode;
    private String orgAddressCountry;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String owner;
    private String image;
    private String logo;
    private String avatar;

    @ManyToMany(mappedBy = "organizations")
    @JsonIgnore
    @ToString.Exclude
    private List<User> user;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name = "organization_employees",
            joinColumns = @JoinColumn(name = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "employees_id")
    )
    private List<Employee> employees;

    @OneToMany(mappedBy = "ownerOrganization", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Product> products;

}
