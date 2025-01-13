package com.udayan.tallykhata.user;

import lombok.Getter;

@Getter
public enum GenderType {
    MALE(1), FEMALE(2), RATHER_NOT_SAY(3), CUSTOM(4);

    private final int value;

    GenderType(int value){
       this.value=value;
    }
}
