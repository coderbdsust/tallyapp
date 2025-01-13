package com.udayan.tallyapp.customexp;

public class UserNotActiveException extends RuntimeException {

    public UserNotActiveException(){
        super();
    }

    public UserNotActiveException(String msg){
        super(msg);
    }

}
