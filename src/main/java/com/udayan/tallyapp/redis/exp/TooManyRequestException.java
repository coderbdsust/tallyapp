package com.udayan.tallyapp.redis.exp;

public class TooManyRequestException extends RuntimeException {
    public TooManyRequestException() {
    }

    public TooManyRequestException(String msg) {
        super(msg);
    }
}
