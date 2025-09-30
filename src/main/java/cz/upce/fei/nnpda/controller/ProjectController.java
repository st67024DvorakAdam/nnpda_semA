package cz.upce.fei.nnpda.controller;

import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // TODO: nahradit pevného uživatele skutečným aktuálním uživatelem z JWT
    private AppUser getCurrentUser() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("test");
        return user;
    }

    @GetMapping
    public List<Project> getProjects() {
        return projectService.getProjectsForUser(getCurrentUser());
    }

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project, getCurrentUser());
    }

    @GetMapping("/{projectId}")
    public Project getProject(@PathVariable Long projectId) {
        return projectService.getProjectByIdAndOwner(projectId, getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Project not found or forbidden"));
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable Long projectId) {
        Project project = projectService.getProjectByIdAndOwner(projectId, getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Project not found or forbidden"));
        projectService.deleteProject(project);
    }

    @PutMapping("/{projectId}")
    public Project updateProject(@PathVariable Long projectId, @RequestBody Project updatedProject) {
        Project project = projectService.getProjectByIdAndOwner(projectId, getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Project not found or forbidden"));

        project.setName(updatedProject.getName());
        project.setDescription(updatedProject.getDescription());
        project.setStatus(updatedProject.getStatus());

        return projectService.updateProject(project);
    }
}
