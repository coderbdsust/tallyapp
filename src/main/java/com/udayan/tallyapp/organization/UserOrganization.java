package com.udayan.tallyapp.organization;

import com.udayan.tallyapp.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users_organizations")
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrganization {

    @EmbeddedId
    private UserOrganizationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizations_id", nullable = false, insertable = false, updatable = false)
    private Organization organization;
}
