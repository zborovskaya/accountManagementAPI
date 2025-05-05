# User Account Management API

##  Описание

Spring Boot приложение для управления пользовательскими аккаунтами, реализующее:

* Авторизацию с использованием JWT токена
* CRUD операции над email/телефонами
* Поиск пользователей с фильтрацией
* Переводы средств между пользователями
* Периодическое начисление процентов на баланс
* Swagger, Redis, логгирование и тесты

## Инструкции по запуску

1. **Создать базу данных Postgres:**

   ```
   CREATE DATABASE user_bank_db;
   ```

2. **Настроить `application.properties`:**
Вставьте свои значения:
spring.datasource.url=jdbc:postgresql://localhost:5432/user_bank_db
spring.datasource.username=<ваш_пользователь>
spring.datasource.password=<ваш_пароль>
spring.datasource.driver-class-name=org.postgresql.Driver
  
3. **Запустить Redis:**
   docker-compose up -d
 
4. **Запустить приложение:**

5. **Swagger UI:**
   http://localhost:8080/swagger-ui/index.html

6. **Пользователи по умолчанию:**

   * `ivan@example.com` / `123456789`
   * `maria@example.com` / `qwertyuio`

# Аутентификация

### Эндпоинт:

```
POST [/api/auth/login](http://localhost:8080/api/auth/login)
```

### Пример запроса:

```json
{
  "identifier": "ivan@example.com",
  "password": "123456789"
}
```

### Ответ:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Примечание:

* `identifier` может быть email или телефон
* JWT-токен вставляется в `Authorization: Bearer <token>`
