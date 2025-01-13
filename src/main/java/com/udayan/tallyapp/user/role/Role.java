package com.udayan.tallyapp.user.role;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.udayan.tallyapp.model.BaseEntity;
import com.udayan.tallyapp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(unique = true)
    private String name;
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    @ToString.Exclude
    private List<User> user;
}
