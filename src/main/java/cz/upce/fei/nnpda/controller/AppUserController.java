package cz.upce.fei.nnpda.controller;

import cz.upce.fei.nnpda.model.dto.auth.AppUserDto;
import cz.upce.fei.nnpda.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping("/users")
    public List<AppUserDto> getAllUsers() {
        return appUserService.getAllUsers();
    }
}
