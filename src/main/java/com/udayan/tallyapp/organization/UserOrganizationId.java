package com.udayan.tallyapp.organization;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganizationId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "organizations_id")
    private UUID organizationsId;
}
