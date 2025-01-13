package com.udayan.tallyapp.customexp;

public class EmailSendingException extends RuntimeException {
  public EmailSendingException(){
    super();
  }
    public EmailSendingException(String message) {
        super(message);
    }
}
