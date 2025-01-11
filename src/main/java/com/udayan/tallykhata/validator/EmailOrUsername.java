package com.udayan.tallykhata.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = { EmailOrUsernameValidator.class })
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailOrUsername {

    String message() default "Invalid username or email format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
