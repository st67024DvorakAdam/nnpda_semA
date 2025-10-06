package cz.upce.fei.nnpda.controller;

import cz.upce.fei.nnpda.model.dto.auth.*;
import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // --- Registrace ---
    @PostMapping("/register")
    public AppUserDto register(@RequestBody @Valid RegisterDto dto) {
        AppUser user = authService.register(dto);
        return AppUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    // --- Login ---
    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginDto dto) {
        return authService.login(dto);
    }

    // --- Request reset hesla ---
    @PostMapping("/request-password-reset")
    public String requestPasswordReset(@RequestBody @Valid PasswordResetRequestDto dto) {
        return authService.requestPasswordReset(dto);
    }

    // --- Reset hesla ---
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody @Valid PasswordResetDto dto) {
        authService.resetPassword(dto);
    }

    // --- Change password (chráněné JWT) ---
    @PostMapping("/change-password")
    public void changePassword(@RequestBody @Valid ChangePasswordDto dto) {
        // získání username z SecurityContext
        String username = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        AppUser currentUser = authService.loadUserByUsername(username);
        authService.changePassword(currentUser, dto);
    }
}
