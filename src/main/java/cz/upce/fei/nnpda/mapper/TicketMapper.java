package cz.upce.fei.nnpda.mapper;

import cz.upce.fei.nnpda.model.dto.ticket.TicketCreateDto;
import cz.upce.fei.nnpda.model.dto.ticket.TicketDto;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import cz.upce.fei.nnpda.model.entity.enums.TicketState;
import cz.upce.fei.nnpda.repository.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    private final AppUserMapper appUserMapper;
    private final TicketHistoryMapper ticketHistoryMapper;
    private final AppUserRepository appUserRepository;

    public TicketMapper(AppUserMapper appUserMapper,
                        TicketHistoryMapper ticketHistoryMapper,
                        AppUserRepository appUserRepository) {
        this.appUserMapper = appUserMapper;
        this.ticketHistoryMapper = ticketHistoryMapper;
        this.appUserRepository = appUserRepository;
    }

    public TicketDto toDto(Ticket ticket) {
        return TicketDto.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .type(ticket.getType())
                .priority(ticket.getPriority())
                .state(ticket.getState())
                .assignee(ticket.getAssignee() != null ? appUserMapper.toDto(ticket.getAssignee()) : null)
                .history(ticket.getHistory().stream()
                        .map(ticketHistoryMapper::toDto)
                        .toList())
                .build();
    }

    public Ticket toEntity(TicketCreateDto dto, Project project) {
        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setType(dto.getType());
        ticket.setPriority(dto.getPriority());
        ticket.setState(TicketState.OPEN); //defaultní stav je OPEN
        ticket.setProject(project);

        if (dto.getAssigneeId() != null) {
            ticket.setAssignee(appUserRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found")));
        }

        return ticket;
    }

}
