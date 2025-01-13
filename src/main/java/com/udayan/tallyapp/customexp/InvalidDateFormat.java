package com.udayan.tallyapp.customexp;

import java.io.IOException;

public class InvalidDateFormat extends IOException {

    public InvalidDateFormat(){
        super();
    }

    public InvalidDateFormat(String msg){
        super(msg);
    }
}
