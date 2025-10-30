package cz.upce.fei.nnpda.controller;

import cz.upce.fei.nnpda.service.GeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/generator")
@RequiredArgsConstructor
public class GeneratorController {

    private final GeneratorService generatorService;

    @PostMapping("/projects")
    public ResponseEntity<String> generateProjects() {
        generatorService.generateProjectsWithTickets();
        return ResponseEntity.ok("Projects with tickets generated successfully");
    }
}

