package com.udayan.tallykhata.customexp;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(){
        super();
    }

    public InvalidDataException(String msg){
        super(msg);
    }
}
