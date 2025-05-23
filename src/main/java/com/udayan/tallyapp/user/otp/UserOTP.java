package com.udayan.tallyapp.user.otp;


import com.udayan.tallyapp.model.BaseEntity;
import com.udayan.tallyapp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="user_otp")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserOTP extends BaseEntity {
    @Column(nullable = false)
    private String otp;
    private Boolean isSend=false;
    private Boolean isUsed=false;
    private Boolean isActive=true;
    private int otpType;
    private LocalDateTime expiryTime;
    private LocalDateTime validatedTime;
    @ManyToOne
    @ToString.Exclude
    private User user;
}
