package com.udayan.tallykhata.user.exp;

public class DuplicateKeyException extends Throwable {

    public DuplicateKeyException(){
        super();
    }

    public DuplicateKeyException(String msg){
        super(msg);
    }
}
