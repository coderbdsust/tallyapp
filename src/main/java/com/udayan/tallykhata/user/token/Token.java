package com.udayan.tallykhata.user.token;

import com.udayan.tallykhata.model.BaseEntity;
import com.udayan.tallykhata.user.User;
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
