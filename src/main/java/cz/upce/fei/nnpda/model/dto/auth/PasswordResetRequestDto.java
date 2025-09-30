package cz.upce.fei.nnpda.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequestDto {

    @NotBlank
    private String usernameOrEmail;
}
