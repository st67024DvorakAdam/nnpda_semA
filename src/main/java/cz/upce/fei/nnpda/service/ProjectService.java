package cz.upce.fei.nnpda.service;

import cz.upce.fei.nnpda.exception.project.ForbiddenException;
import cz.upce.fei.nnpda.exception.project.ProjectNotFoundException;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project> getProjectsForUser(AppUser user) {
        return projectRepository.findByOwner(user);
    }

    public Project createProject(Project project, AppUser owner) {
        project.setOwner(owner);
        return projectRepository.save(project);
    }

    public Project getProjectByIdAndOwner(Long projectId, AppUser owner) {
        return projectRepository.findByIdAndOwner(projectId, owner)
                .orElseThrow(() -> new ForbiddenException("Access to foreign project is forbidden"));
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }

    public Project updateProject(Project project) {
        return projectRepository.save(project);
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }
}

