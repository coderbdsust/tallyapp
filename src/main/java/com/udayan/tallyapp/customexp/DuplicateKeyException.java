package com.udayan.tallykhata.customexp;

public class DuplicateKeyException extends RuntimeException {

    public DuplicateKeyException(){
        super();
    }

    public DuplicateKeyException(String msg){
        super(msg);
    }
}
