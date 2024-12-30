package com.udayan.tallykhata.user.otp;


import com.udayan.tallykhata.model.BaseEntity;
import com.udayan.tallykhata.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="user_otp")
@Data
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
    private User user;
}
