package cz.upce.fei.nnpda.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetDto {

    @NotBlank
    private String code;

    @NotBlank
    @Size(min = 6)
    private String newPassword;
}
