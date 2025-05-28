package com.udayan.tallyapp.user.token;

import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS_TOKEN(1),
    REFRESH_TOKEN(2),
    PENDING_LOGIN_TOKEN(3);

    private final int value;

    TokenType(int value){
        this.value=value;
    }
}
