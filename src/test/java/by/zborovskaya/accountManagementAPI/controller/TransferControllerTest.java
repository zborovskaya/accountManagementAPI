package by.zborovskaya.accountManagementAPI.controller;

import by.zborovskaya.accountManagementAPI.config.SecurityConfigTest;
import by.zborovskaya.accountManagementAPI.model.Account;
import by.zborovskaya.accountManagementAPI.model.User;
import by.zborovskaya.accountManagementAPI.repository.AccountRepository;
import by.zborovskaya.accountManagementAPI.repository.UserRepository;
import by.zborovskaya.accountManagementAPI.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfigTest.class)
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.yml")
class TransferControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("root");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String jwtToken;
    private Long accountFromId;
    private Long accountToId;

    @BeforeEach
    void setup() {
        User fromUser = new User();
        fromUser.setName("testuser1");
        fromUser.setPassword("password");
        userRepository.save(fromUser);

        Account fromAccount = new Account();
        fromAccount.setUser(fromUser);
        fromAccount.setBalance(BigDecimal.valueOf(500.0));
        accountRepository.save(fromAccount);
        accountFromId = fromAccount.getId();

        User toUser = new User();
        toUser.setName("testuser2");
        toUser.setPassword("password");
        userRepository.save(toUser);

        Account toAccount = new Account();
        toAccount.setUser(toUser);
        toAccount.setBalance(BigDecimal.valueOf(100.0));
        accountRepository.save(toAccount);
        accountToId = toAccount.getId();

        // Генерация JWT токена для пользователя
        jwtToken = jwtUtil.generateToken(fromUser.getId());
    }

    @Test
    void testTransferSuccess() throws Exception {
        String requestJson = String.format(""" 
            {
                "toUserId": %d,
                "amount": 100.0
            }
            """, accountToId);

        // Выполнение запроса с проверкой успешного ответа
        mockMvc.perform(post("/api/transfer")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful"));
    }

    @Test
    void testTransferFailureInsufficientBalance() throws Exception {
        // Создание JSON тела для запроса с недостаточным балансом
        String requestJson = String.format("""
            {
                "toUserId": %d,
                "amount": 600.0
            }
            """, accountToId);
        mockMvc.perform(post("/api/transfer")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));
    }

    @Test
    void testTransferFailureTransferToSelf() throws Exception {
        String requestJson = String.format("""
            {
                "toUserId": %d,
                "amount": 100.0
            }
            """, accountFromId);

        // Выполнение запроса с проверкой ошибки перевода на свой аккаунт
        mockMvc.perform(post("/api/transfer")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot transfer to yourself"));
    }

    @Test
    void testTransferFailureInvalidAmount() throws Exception {
        String requestJson = String.format("""
            {
                "toUserId": %d,
                "amount": 0.0
            }
            """, accountToId);

        mockMvc.perform(post("/api/transfer")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Transfer amount must be > 0"));
    }
}
