package by.zborovskaya.accountManagementAPI.service;

import by.zborovskaya.accountManagementAPI.dto.TransferRequest;
import by.zborovskaya.accountManagementAPI.model.Account;
import by.zborovskaya.accountManagementAPI.model.User;
import by.zborovskaya.accountManagementAPI.repository.AccountRepository;
import by.zborovskaya.accountManagementAPI.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private TransferService transferService;

    private User fromUser;
    private User toUser;
    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    public void setUp() {
        // Инициализация пользователей и аккаунтов
        fromUser = new User();
        fromUser.setId(1L);
        toUser = new User();
        toUser.setId(2L);

        fromAccount = new Account();
        fromAccount.setUser(fromUser);
        fromAccount.setBalance(BigDecimal.valueOf(100));

        toAccount = new Account();
        toAccount.setUser(toUser);
        toAccount.setBalance(BigDecimal.valueOf(50));

        when(authService.getCurrentAuthenticatedUser()).thenReturn(fromUser);
        when(userRepository.findById(fromUser.getId())).thenReturn(Optional.of(fromUser));
        when(accountRepository.findByUserIdWithLock(fromUser.getId())).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(toUser.getId())).thenReturn(Optional.of(toAccount));
    }

    @Test
    public void testSuccessfulTransfer() {
        TransferRequest request = new TransferRequest();
        request.setToUserId(2L);
        request.setAmount(BigDecimal.valueOf(30));

        transferService.transferMoney(request);

        assertEquals(BigDecimal.valueOf(70), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(80), toAccount.getBalance());

        verify(accountRepository, times(2)).save(any(Account.class));  // Дважды сохраняем аккаунты
    }

    @Test
    public void testTransferToSelfThrowsException() {
        TransferRequest request = new TransferRequest();
        request.setToUserId(1L); // Переводим себе
        request.setAmount(BigDecimal.valueOf(30));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferService.transferMoney(request);
        });

        assertEquals("Cannot transfer to yourself", exception.getMessage());
    }

    @Test
    public void testInsufficientBalanceThrowsException() {
        TransferRequest request = new TransferRequest();
        request.setToUserId(2L);
        request.setAmount(BigDecimal.valueOf(150)); // Слишком большая сумма для отправителя

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferService.transferMoney(request);
        });

        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    public void testInvalidAmountThrowsException() {
        TransferRequest request = new TransferRequest();
        request.setToUserId(2L);
        request.setAmount(BigDecimal.valueOf(0)); // Нулевая сумма перевода

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferService.transferMoney(request);
        });

        assertEquals("Transfer amount must be > 0", exception.getMessage());
    }
}
