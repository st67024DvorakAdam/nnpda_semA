package cz.upce.fei.nnpda.model.dto.project;

import cz.upce.fei.nnpda.model.entity.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 120)
    private String name;

    private String description;

    private ProjectStatus status;
}