package cz.upce.fei.nnpda.model.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppUserDto {
    private Long id;
    private String username;
    private String email;
}

