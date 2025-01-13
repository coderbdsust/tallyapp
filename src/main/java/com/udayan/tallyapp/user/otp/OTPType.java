package com.udayan.tallyapp.user.otp;

import lombok.Getter;

@Getter
public enum OTPType {
    ACCOUNT_VERIFICATION(1),
    ACCOUNT_LOGIN(2),
    PASSWORD_RESET(3);

    private final int name;

    OTPType(int name) {
        this.name = name;
    }

}
