package by.zborovskaya.accountManagementAPI.repository;

import by.zborovskaya.accountManagementAPI.model.EmailData;
import by.zborovskaya.accountManagementAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
    Optional<EmailData> findByUserAndEmail(User user, String email);
    boolean existsByEmail(String email);
}

