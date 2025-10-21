package cz.upce.fei.nnpda.mapper;

import cz.upce.fei.nnpda.model.dto.auth.AppUserDto;
import cz.upce.fei.nnpda.model.entity.AppUser;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {

    public AppUserDto toDto(AppUser user) {
        if (user == null) return null;
        return AppUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public AppUser toEntity(AppUserDto dto) {
        if (dto == null) return null;
        AppUser user = new AppUser();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // password není z DTO, nastavuje se jen při registraci / změně hesla
        return user;
    }
}
