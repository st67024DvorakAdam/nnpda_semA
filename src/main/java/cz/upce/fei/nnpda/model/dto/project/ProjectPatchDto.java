package cz.upce.fei.nnpda.model.dto.project;

import cz.upce.fei.nnpda.model.entity.enums.ProjectStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProjectPatchDto {
    @Size(min = 1, max = 120)
    private String name;
    private String description;
    private ProjectStatus status;
}

