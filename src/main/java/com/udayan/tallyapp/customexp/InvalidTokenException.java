package com.udayan.tallyapp.customexp;

import java.io.IOException;

public class InvalidTokenException extends IOException {
    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(String msg) {
        super(msg);
    }
}
