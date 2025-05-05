package by.zborovskaya.accountManagementAPI.service;

import by.zborovskaya.accountManagementAPI.dto.EmailUpdateRequest;
import by.zborovskaya.accountManagementAPI.dto.PhoneUpdateRequest;
import by.zborovskaya.accountManagementAPI.dto.UserDto;
import by.zborovskaya.accountManagementAPI.mapper.UserMapper;
import by.zborovskaya.accountManagementAPI.model.EmailData;
import by.zborovskaya.accountManagementAPI.model.PhoneData;
import by.zborovskaya.accountManagementAPI.model.User;
import by.zborovskaya.accountManagementAPI.repository.EmailDataRepository;
import by.zborovskaya.accountManagementAPI.repository.PhoneDataRepository;
import by.zborovskaya.accountManagementAPI.repository.UserRepository;
import by.zborovskaya.accountManagementAPI.repository.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailDataRepository emailRepo;
    private final PhoneDataRepository phoneRepo;
    private final AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    @Transactional
    public Page<UserDto> searchUsers(String name, String email, String phone,
                                     LocalDate dateOfBirth, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        UserSpecification specification = new UserSpecification(name, email, phone, dateOfBirth);
        logger.info("Searching for users with filters: name={}, email={}, phone={}, dateOfBirth={}, page={}, size={}",
                name, email, phone, dateOfBirth, page, size);
        Page<User> users = userRepository.findAll(specification, pageable);

        users.getContent().forEach(user -> {
            user.getEmails().size();
            user.getPhones().size();
            if (user.getAccount() != null) user.getAccount().getBalance();
        });

        List<UserDto> userDtos = UserMapper.toDtoList(users.getContent());

        return new PageImpl<>(userDtos, pageable, users.getTotalElements());
    }

    @Override
    @Transactional
    public UserDto getCurrentUser() {
        Long userId = authService.getCurrentAuthenticatedUser().getId();
        logger.info("Fetching details for the current user with ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Hibernate.initialize(user.getEmails());
        Hibernate.initialize(user.getPhones());
        if (user.getAccount() != null) Hibernate.initialize(user.getAccount());

        logger.info("Successfully fetched user details for ID: {}", userId);
        return UserMapper.toDto(user);
    }

    @Transactional
    @Override
    public void updateEmails(EmailUpdateRequest request) {
        Long userId = authService.getCurrentAuthenticatedUser().getId();
        logger.info("Updating emails for user with ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        long existingCount = user.getEmails().size();
        long toRemoveCount = request.getEmailsToRemove().size();
        long toRemain = existingCount - toRemoveCount + request.getEmailsToAdd().size();
        if (toRemain < 1) {
            throw new IllegalStateException("User must have at least one email");
        }

        for (String email : request.getEmailsToAdd()) {
            if (emailRepo.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already in use: " + email);
            }
            EmailData emailData = new EmailData(null, user, email);
            emailRepo.save(emailData);
            user.getEmails().add(emailData);
            logger.info("Added new email {} for user {}", email, userId);
        }

        for (String email : request.getEmailsToRemove()) {
            EmailData data = emailRepo.findByUserAndEmail(user, email)
                    .orElseThrow(() -> new IllegalArgumentException("Email not found: " + email));
            user.getEmails().remove(data);
            emailRepo.delete(data);
            logger.info("Removed email {} for user {}", email, userId);
        }
    }

    @Transactional
    @Override
    public void updatePhones(PhoneUpdateRequest request) {
        Long userId = authService.getCurrentAuthenticatedUser().getId();
        logger.info("Updating phones for user with ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        long existingCount = user.getPhones().size();
        long toRemoveCount = request.getPhonesToRemove().size();
        long toRemain = existingCount - toRemoveCount + request.getPhonesToAdd().size();
        if (toRemain < 1) {
            throw new IllegalStateException("User must have at least one phone number");
        }

        for (String phone : request.getPhonesToAdd()) {
            if (phoneRepo.existsByPhone(phone)) {
                throw new IllegalArgumentException("Phone already in use: " + phone);
            }
            PhoneData phoneData = new PhoneData(null, user, phone);
            phoneRepo.save(phoneData);
            user.getPhones().add(phoneData);
            logger.info("Added new phone {} for user {}", phone, userId);
        }

        for (String phone : request.getPhonesToRemove()) {
            PhoneData data = phoneRepo.findByUserAndPhone(user, phone)
                    .orElseThrow(() -> new IllegalArgumentException("Phone not found: " + phone));
            user.getPhones().remove(data);
            phoneRepo.delete(data);
            logger.info("Removed phone {} for user {}", phone, userId);
        }
    }
}

