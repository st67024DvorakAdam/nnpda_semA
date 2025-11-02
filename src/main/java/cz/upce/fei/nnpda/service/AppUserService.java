package cz.upce.fei.nnpda.service;

import cz.upce.fei.nnpda.model.dto.auth.AppUserDto;
import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public List<AppUserDto> getAllUsers() {
        return appUserRepository.findAll().stream()
                .map(u -> AppUserDto.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .build())
                .collect(Collectors.toList());
    }
}
