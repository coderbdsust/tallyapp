package com.udayan.tallyapp.user.address;

import com.udayan.tallyapp.model.BaseEntity;
import com.udayan.tallyapp.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "user_address")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Address extends BaseEntity {
    private String addressLine;
    private String city;
    private String state;
    private String postCode;
    private String country;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
}
