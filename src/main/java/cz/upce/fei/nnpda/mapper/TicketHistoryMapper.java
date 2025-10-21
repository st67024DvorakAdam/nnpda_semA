package cz.upce.fei.nnpda.mapper;

import cz.upce.fei.nnpda.model.dto.ticketHistory.TicketHistoryDto;
import cz.upce.fei.nnpda.model.entity.TicketHistory;
import org.springframework.stereotype.Component;

@Component
public class TicketHistoryMapper {

    private final AppUserMapper appUserMapper;

    public TicketHistoryMapper(AppUserMapper appUserMapper) {
        this.appUserMapper = appUserMapper;
    }

    public TicketHistoryDto toDto(TicketHistory history) {
        if (history == null) return null;
        return TicketHistoryDto.builder()
                .id(history.getId())
                .state(history.getState())
                .priority(history.getPriority())
                .assignee(appUserMapper.toDto(history.getAssignee()))
                .changeTime(history.getChangeTime())
                .build();
    }
}
