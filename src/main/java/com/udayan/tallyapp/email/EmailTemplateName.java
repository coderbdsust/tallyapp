package com.udayan.tallykhata.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),
    FORGOT_PASSWORD_OTP("forgot_password_otp"),
    GENERIC_MESSAGE_MAIL("generic_message_mail");

    private final String name;
    EmailTemplateName(String name) {
        this.name = name;
    }
}
