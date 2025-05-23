package com.udayan.tallyapp.user;


import com.udayan.tallyapp.model.BaseEntity;
import com.udayan.tallyapp.organization.Organization;
import com.udayan.tallyapp.user.address.Address;
import com.udayan.tallyapp.user.shortprofile.ShortProfile;
import com.udayan.tallyapp.user.role.Role;
import com.udayan.tallyapp.user.token.Token;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails, Principal {

    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    private String salt;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = true)
    private String mobileNo;
    @Enumerated(EnumType.STRING)
    private GenderType gender;
    private Boolean isMobileNumberVerified=false;
    private String fullName;
    private LocalDate dateOfBirth;
    private Boolean enabled = false;
    private Boolean accountLocked=false;
    private Boolean tfaEnabled=false;
    private Boolean tfaByEmail=false;
    private Boolean tfaByMobile=false;
    @ManyToMany(fetch = EAGER)
    private List<Role> roles;
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;
    @OneToMany(mappedBy = "user")
    private List<Address> addresses;
    @OneToMany(mappedBy = "user")
    List<ShortProfile> shortProfiles;
    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name = "users_organizations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "organizations_id")
    )
    private List<Organization> organizations;

    @Override
    public String getName() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public ArrayList<String> getUserRoles() {
        return this.roles.stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles
                .stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                .collect(Collectors.toList());
    }
}
