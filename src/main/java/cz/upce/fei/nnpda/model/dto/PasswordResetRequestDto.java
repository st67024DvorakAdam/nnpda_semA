package cz.upce.fei.nnpda.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequestDto {

    @NotBlank
    private String usernameOrEmail;
}
