package cz.upce.fei.nnpda.model.dto.ticketHistory;

import cz.upce.fei.nnpda.model.dto.auth.AppUserDto;
import cz.upce.fei.nnpda.model.entity.enums.TicketPriority;
import cz.upce.fei.nnpda.model.entity.enums.TicketState;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketHistoryDto {
    private Long id;
    private TicketState state;
    private TicketPriority priority;
    private AppUserDto assignee;
    private LocalDateTime changeTime;
}
