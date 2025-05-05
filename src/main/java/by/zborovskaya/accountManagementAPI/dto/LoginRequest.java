package by.zborovskaya.accountManagementAPI.dto;

import by.zborovskaya.accountManagementAPI.validation.ValidIdentifier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Логин обязателен")
    @ValidIdentifier(message = "Логин должен быть либо email, либо телефон")
    private String identifier; // Может быть email или телефон

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, max = 500, message = "Пароль должен содержать от 8 до 500 символов")
    private String password;
}
