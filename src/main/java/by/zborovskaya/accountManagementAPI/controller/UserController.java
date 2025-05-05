package by.zborovskaya.accountManagementAPI.controller;

import by.zborovskaya.accountManagementAPI.dto.EmailUpdateRequest;
import by.zborovskaya.accountManagementAPI.dto.PhoneUpdateRequest;
import by.zborovskaya.accountManagementAPI.dto.UserDto;
import by.zborovskaya.accountManagementAPI.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public Page<UserDto> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateOfBirth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.searchUsers(name, email, phone, dateOfBirth, page, size);
    }

    @GetMapping("/me")
    public UserDto getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PutMapping("/me/email")
    public ResponseEntity<Void> updateEmails(@RequestBody EmailUpdateRequest request) {
        userService.updateEmails(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/phone")
    public ResponseEntity<Void> updatePhones(@RequestBody PhoneUpdateRequest request) {
        userService.updatePhones(request);
        return ResponseEntity.noContent().build();
    }
}

