package com.udayan.tallyapp.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.EmailValidator;

public class EmailOrUsernameValidator implements ConstraintValidator<EmailOrUsername, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false; // You can adjust this to allow nulls
        }

        // Validate using @Email equivalent
        boolean isEmail = EmailValidator.getInstance().isValid(value);

        // Validate using a custom pattern
        String usernameRegex = "^(?!.*[._]{2})[a-zA-Z0-9](?!.*[._]$)[a-zA-Z0-9._]{2,20}[a-zA-Z0-9]$";
        boolean matchesUsername = value.matches(usernameRegex);

        return isEmail || matchesUsername; // Pass if either condition is true
    }
}