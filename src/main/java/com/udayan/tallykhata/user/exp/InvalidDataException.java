package com.udayan.tallykhata.user.exp;

public class InvalidDataException extends Throwable {
    public InvalidDataException(){
        super();
    }

    public InvalidDataException(String msg){
        super(msg);
    }
}
