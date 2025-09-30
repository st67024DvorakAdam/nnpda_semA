package cz.upce.fei.nnpda.service;

import cz.upce.fei.nnpda.exception.UserNotFoundException;
import cz.upce.fei.nnpda.model.dto.*;
import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.model.token.PasswordResetToken;
import cz.upce.fei.nnpda.repository.AppUserRepository;
import cz.upce.fei.nnpda.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Dočasné uložení reset kódů
    private final Map<String, PasswordResetToken> passwordResetTokens = new HashMap<>();

    // --- Registrace ---
    public AppUser register(RegisterDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        AppUser user = AppUser.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        return userRepository.save(user);
    }

    // --- Login ---
    public String login(LoginDto dto) {
        AppUser user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );

        return jwtUtil.generateToken(userDetails);
    }

    // --- Request reset hesla ---
    public String requestPasswordReset(PasswordResetRequestDto dto) {
        AppUser user = userRepository.findByUsername(dto.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(dto.getUsernameOrEmail()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String code = UUID.randomUUID().toString();
        long expiresAt = System.currentTimeMillis() + 15 * 60 * 1000; // 15 minut

        PasswordResetToken token = new PasswordResetToken(code, user.getUsername(), expiresAt);

        // přepíše starý token, pokud už existoval
        passwordResetTokens.put(user.getUsername(), token);

        return code;
    }

    // --- Reset hesla ---
    public void resetPassword(PasswordResetDto dto) {
        // najití reset tokenu podle username
        PasswordResetToken token = passwordResetTokens.values().stream()
                .filter(t -> t.getCode().equals(dto.getCode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid or expired code"));

        if (token.isExpired()) {
            passwordResetTokens.remove(token.getUsername());
            throw new RuntimeException("Code expired");
        }

        AppUser user = userRepository.findByUsername(token.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // výmaz po použití
        passwordResetTokens.remove(token.getUsername());
    }

    // --- Change password (pro přihlášeného uživatele) ---
    public void changePassword(AppUser user, ChangePasswordDto dto) {
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    public AppUser loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

}
