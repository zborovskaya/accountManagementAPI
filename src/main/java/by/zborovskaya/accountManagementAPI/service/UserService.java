package by.zborovskaya.accountManagementAPI.service;

import by.zborovskaya.accountManagementAPI.dto.EmailUpdateRequest;
import by.zborovskaya.accountManagementAPI.dto.PhoneUpdateRequest;
import by.zborovskaya.accountManagementAPI.dto.UserDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface UserService {

    Page<UserDto> searchUsers(String name, String email, String phone, LocalDate dateOfBirth, int page, int size);

    UserDto getCurrentUser();

    void updateEmails(EmailUpdateRequest request);

    void updatePhones(PhoneUpdateRequest request);
}

