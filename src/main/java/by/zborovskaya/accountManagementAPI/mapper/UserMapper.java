package by.zborovskaya.accountManagementAPI.mapper;

import by.zborovskaya.accountManagementAPI.dto.UserDto;
import by.zborovskaya.accountManagementAPI.model.EmailData;
import by.zborovskaya.accountManagementAPI.model.PhoneData;
import by.zborovskaya.accountManagementAPI.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;

        List<String> emailList = user.getEmails() != null
                ? user.getEmails().stream().map(EmailData::getEmail).toList()
                : Collections.emptyList();

        List<String> phoneList = user.getPhones() != null
                ? user.getPhones().stream().map(PhoneData::getPhone).toList()
                : Collections.emptyList();

        BigDecimal balance = user.getAccount() != null ? user.getAccount().getBalance() : null;

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getDateOfBirth(),
                emailList,
                phoneList,
                balance
        );
    }

    public static List<UserDto> toDtoList(List<User> users) {
        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }
}

