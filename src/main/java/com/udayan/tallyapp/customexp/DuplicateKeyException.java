package com.udayan.tallyapp.customexp;

public class DuplicateKeyException extends RuntimeException {

    public DuplicateKeyException(){
        super();
    }

    public DuplicateKeyException(String msg){
        super(msg);
    }
}
