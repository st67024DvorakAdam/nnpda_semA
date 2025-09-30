package cz.upce.fei.nnpda.model.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectCreateDto {
    @NotBlank
    @Size(min = 1, max = 120)
    private String name;

    private String description; // volitelné
}
