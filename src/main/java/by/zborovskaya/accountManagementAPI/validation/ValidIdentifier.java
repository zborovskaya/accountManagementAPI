package by.zborovskaya.accountManagementAPI.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IdentifierValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIdentifier {
    String message() default "Неверный формат логина. Логин должен быть либо email, либо телефон.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

