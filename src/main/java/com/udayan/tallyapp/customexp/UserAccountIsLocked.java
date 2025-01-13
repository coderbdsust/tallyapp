package com.udayan.tallyapp.customexp;

public class UserAccountIsLocked extends RuntimeException {

    public UserAccountIsLocked(){
        super();
    }

    public UserAccountIsLocked(String msg){
        super(msg);
    }
}
