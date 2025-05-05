package by.zborovskaya.accountManagementAPI.service;

import by.zborovskaya.accountManagementAPI.model.Account;
import by.zborovskaya.accountManagementAPI.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceSchedulerService {

    private final AccountRepository accountRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final BigDecimal MAX_MULTIPLIER = BigDecimal.valueOf(2.07);
    private static final BigDecimal INCREMENT_PERCENT = BigDecimal.valueOf(0.10);
    private static final Logger logger = LoggerFactory.getLogger(BalanceSchedulerService.class);

    @Scheduled(fixedRate = 30_000)
    public void increaseBalancePeriodically() {
        logger.info("Balance update task started");
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            String redisKey = "balance_updated:" + account.getId();

            BigDecimal initial = account.getInitialDeposit();
            BigDecimal maxAllowed = initial.multiply(MAX_MULTIPLIER);

            if (account.getBalance().compareTo(maxAllowed) >= 0) {
                continue;
            }

            BigDecimal newBalance = account.getBalance().multiply(BigDecimal.ONE.add(INCREMENT_PERCENT));
            if (newBalance.compareTo(maxAllowed) > 0) {
                newBalance = maxAllowed;
            }

            account.setBalance(newBalance);
            accountRepository.save(account);


            redisTemplate.opsForValue().set(redisKey, String.valueOf(System.currentTimeMillis()));
            logger.info("Redis key {} set with current timestamp for account {}", redisKey, account.getId());
        }
    }
}

