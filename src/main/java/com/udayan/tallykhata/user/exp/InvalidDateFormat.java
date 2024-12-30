package com.udayan.tallykhata.user.exp;

import java.io.IOException;

public class InvalidDateFormat extends IOException {

    public InvalidDateFormat(){
        super();
    }

    public InvalidDateFormat(String msg){
        super(msg);
    }
}
