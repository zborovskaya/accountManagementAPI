package by.zborovskaya.accountManagementAPI.repository;

import by.zborovskaya.accountManagementAPI.model.PhoneData;
import by.zborovskaya.accountManagementAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {
    Optional<PhoneData> findByUserAndPhone(User user, String phone);
    boolean existsByPhone(String phone);
}

