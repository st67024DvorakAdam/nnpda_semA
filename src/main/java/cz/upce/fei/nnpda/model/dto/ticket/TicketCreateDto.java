package cz.upce.fei.nnpda.model.dto.ticket;

import cz.upce.fei.nnpda.model.entity.enums.TicketPriority;
import cz.upce.fei.nnpda.model.entity.enums.TicketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketCreateDto {
    @NotBlank
    private String title;

    @NotNull
    private TicketType type;
    @NotNull
    private TicketPriority priority;
}
