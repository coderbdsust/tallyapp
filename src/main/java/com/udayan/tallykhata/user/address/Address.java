package com.udayan.tallykhata.user.address;

import com.udayan.tallykhata.model.BaseEntity;
import com.udayan.tallykhata.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Table(name = "user_address")
@Entity
@Data
public class Address extends BaseEntity {
    private String houseName;
    private String roadName;
    private String villageName;
    private String city;
    private String postCode;
    private String postOffice;
    private String state;
    private String country;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
