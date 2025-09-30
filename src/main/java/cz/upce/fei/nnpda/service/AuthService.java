package cz.upce.fei.nnpda.service;

import cz.upce.fei.nnpda.model.dto.*;
import cz.upce.fei.nnpda.model.entity.AppUser;
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

    // Dočasné uložení reset kódů; v reálu použít DB s expirací
    private final Map<String, String> passwordResetCodes = new HashMap<>();

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
        Optional<AppUser> userOpt = userRepository.findByUsername(dto.getUsernameOrEmail());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(dto.getUsernameOrEmail());
        }
        AppUser user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

        String code = UUID.randomUUID().toString();
        passwordResetCodes.put(code, user.getUsername()); // map code -> username
        // V reálu by se kód poslal emailem

        return code; // pro testování vrátíme kód
    }

    // --- Reset hesla ---
    public void resetPassword(PasswordResetDto dto) {
        String username = passwordResetCodes.get(dto.getCode());
        if (username == null) {
            throw new RuntimeException("Invalid or expired code");
        }

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        passwordResetCodes.remove(dto.getCode()); // kód jednorázový
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
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
