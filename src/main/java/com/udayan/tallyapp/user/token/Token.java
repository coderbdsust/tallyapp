package com.udayan.tallyapp.user.token;

import com.udayan.tallyapp.model.BaseEntity;
import com.udayan.tallyapp.user.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token extends BaseEntity {

    @Column(unique = true, length = 512)
    private String token;

    private boolean revoked;

    private boolean expired;

    private int tokenType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
}
