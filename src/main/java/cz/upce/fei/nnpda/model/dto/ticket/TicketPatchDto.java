package cz.upce.fei.nnpda.model.dto.ticket;

import cz.upce.fei.nnpda.model.entity.enums.TicketPriority;
import cz.upce.fei.nnpda.model.entity.enums.TicketState;
import cz.upce.fei.nnpda.model.entity.enums.TicketType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketPatchDto {

    @Size(min = 1, max = 160)
    private String title;

    private TicketType type;

    private TicketPriority priority;

    private TicketState state;
}
