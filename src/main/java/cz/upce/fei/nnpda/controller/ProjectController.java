package cz.upce.fei.nnpda.controller;

import cz.upce.fei.nnpda.mapper.ProjectMapper;
import cz.upce.fei.nnpda.model.dto.project.ProjectDto;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.service.AuthService;
import cz.upce.fei.nnpda.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final AuthService authService;
    private final ProjectMapper projectMapper;

    private AppUser getCurrentUser() {
        String username = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return authService.loadUserByUsername(username);
    }

    @GetMapping
    public List<ProjectDto> getProjects() {
        return projectService.getProjectsForUser(getCurrentUser())
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @PostMapping
    public ProjectDto createProject(@RequestBody @Valid ProjectDto projectDto) {
        Project project = projectService.createProject(projectMapper.toEntity(projectDto, getCurrentUser()), getCurrentUser());
        return projectMapper.toDto(project);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProject(@PathVariable Long projectId) {
        Project project = projectService.getProjectByIdAndOwner(projectId, getCurrentUser());
        return projectMapper.toDto(project);
    }

    @PutMapping("/{projectId}")
    public ProjectDto updateProject(@PathVariable Long projectId, @RequestBody @Valid ProjectDto projectDto) {
        Project project = projectService.getProjectByIdAndOwner(projectId, getCurrentUser());
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setStatus(projectDto.getStatus());
        return projectMapper.toDto(projectService.updateProject(project));
    }

    @PatchMapping("/{projectId}")
    public ProjectDto patchProject(@PathVariable Long projectId,
                                   @RequestBody ProjectDto projectDto) {
        Project project = projectService.getProjectByIdAndOwner(projectId, getCurrentUser());

        if (projectDto.getName() != null) project.setName(projectDto.getName());
        if (projectDto.getDescription() != null) project.setDescription(projectDto.getDescription());
        if (projectDto.getStatus() != null) project.setStatus(projectDto.getStatus());

        Project updated = projectService.updateProject(project);
        return projectMapper.toDto(updated);
    }


    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable Long projectId) {
        Project project = projectService.getProjectByIdAndOwner(projectId, getCurrentUser());
        projectService.deleteProject(project);
    }

}

