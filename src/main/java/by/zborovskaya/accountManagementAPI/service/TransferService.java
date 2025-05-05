package by.zborovskaya.accountManagementAPI.service;

import by.zborovskaya.accountManagementAPI.dto.TransferRequest;
import by.zborovskaya.accountManagementAPI.model.Account;
import by.zborovskaya.accountManagementAPI.model.User;
import by.zborovskaya.accountManagementAPI.repository.AccountRepository;
import by.zborovskaya.accountManagementAPI.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    @Transactional
    public void transferMoney(TransferRequest request) {
        Long userId = authService.getCurrentAuthenticatedUser().getId();
        User fromUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (fromUser.getId().equals(request.getToUserId())) {
            throw new IllegalArgumentException("Cannot transfer to yourself");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be > 0");
        }

        Account fromAccount = accountRepository.findByUserIdWithLock(fromUser.getId())
                .orElseThrow(() -> new RuntimeException("From account not found"));

        Account toAccount = accountRepository.findByUserIdWithLock(request.getToUserId())
                .orElseThrow(() -> new RuntimeException("To account not found"));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        logger.info("Successfully transferred {} from user {} to user {}", request.getAmount(), fromUser.getId(), request.getToUserId());
    }
}

