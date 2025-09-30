package cz.upce.fei.nnpda.mapper;

import cz.upce.fei.nnpda.model.dto.project.ProjectCreateDto;
import cz.upce.fei.nnpda.model.dto.project.ProjectDto;
import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.enums.ProjectStatus;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectDto toDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .build();
    }

    public Project toEntity(ProjectDto dto, AppUser owner) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus() != null ? dto.getStatus() : ProjectStatus.ACTIVE);
        project.setOwner(owner);
        return project;
    }

    public Project toEntity(ProjectCreateDto dto, AppUser owner) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setOwner(owner);
        project.setStatus(ProjectStatus.ACTIVE); // defaultně ACTIVE
        return project;
    }

}
