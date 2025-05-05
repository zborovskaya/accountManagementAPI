package by.zborovskaya.accountManagementAPI.service;

import by.zborovskaya.accountManagementAPI.dto.AuthResponse;
import by.zborovskaya.accountManagementAPI.dto.LoginRequest;
import by.zborovskaya.accountManagementAPI.model.CustomUserDetails;
import by.zborovskaya.accountManagementAPI.model.User;
import by.zborovskaya.accountManagementAPI.repository.UserRepository;
import by.zborovskaya.accountManagementAPI.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt;

        if (request.getIdentifier().contains("@")) {
            userOpt = userRepository.findByEmail(request.getIdentifier());
        } else {
            userOpt = userRepository.findByPhoneNumber(request.getIdentifier());
        }

        User user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getId());

        return new AuthResponse(token);
    }

    public User getCurrentAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("User not authenticated");
        }
        return ((CustomUserDetails) auth.getPrincipal()).getUser();
    }

}

