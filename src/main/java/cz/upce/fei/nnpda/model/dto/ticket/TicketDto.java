package cz.upce.fei.nnpda.model.dto.ticket;

import cz.upce.fei.nnpda.model.entity.enums.TicketPriority;
import cz.upce.fei.nnpda.model.entity.enums.TicketState;
import cz.upce.fei.nnpda.model.entity.enums.TicketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDto {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 160)
    private String title;

    @NotNull
    private TicketType type;

    @NotNull
    private TicketPriority priority;

    private TicketState state;
}

