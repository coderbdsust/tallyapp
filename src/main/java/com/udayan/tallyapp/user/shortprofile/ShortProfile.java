package com.udayan.tallyapp.user.shortprofile;

import com.udayan.tallyapp.model.BaseEntity;
import com.udayan.tallyapp.user.User;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="short_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortProfile extends BaseEntity {

    private String designation;
    private String skills;
    private String companyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
}
