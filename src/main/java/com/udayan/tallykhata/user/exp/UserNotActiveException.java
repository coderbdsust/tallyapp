package com.udayan.tallykhata.user.exp;

public class UserNotActiveException extends Throwable {

    public UserNotActiveException(){
        super();
    }

    public UserNotActiveException(String msg){
        super(msg);
    }

}
