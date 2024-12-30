package com.udayan.tallykhata.user.exp;

public class UserAccountIsLocked extends Throwable {

    public UserAccountIsLocked(){
        super();
    }

    public UserAccountIsLocked(String msg){
        super(msg);
    }
}
