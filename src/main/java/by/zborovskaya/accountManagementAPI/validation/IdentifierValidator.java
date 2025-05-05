package by.zborovskaya.accountManagementAPI.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class IdentifierValidator implements ConstraintValidator<ValidIdentifier, String> {

    private static final String PHONE_REGEX = "^\\d{11}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";  // формат email

    @Override
    public void initialize(ValidIdentifier constraintAnnotation) {
        // Инициализация, если необходимо
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;  // Если поле пустое, считаем его невалидным
        }
        // Проверка, является ли значение либо валидным номером телефона, либо валидным email
        return Pattern.matches(PHONE_REGEX, value) || Pattern.matches(EMAIL_REGEX, value);
    }
}

