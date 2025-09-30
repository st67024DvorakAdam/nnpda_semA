package cz.upce.fei.nnpda.mapper;

import cz.upce.fei.nnpda.model.dto.ticket.TicketCreateDto;
import cz.upce.fei.nnpda.model.dto.ticket.TicketDto;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import cz.upce.fei.nnpda.model.entity.enums.TicketState;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketDto toDto(Ticket ticket) {
        return TicketDto.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .type(ticket.getType())
                .priority(ticket.getPriority())
                .state(ticket.getState())
                .build();
    }

    public Ticket toEntity(TicketDto dto, Project project) {
        return Ticket.builder()
                .title(dto.getTitle())
                .type(dto.getType())
                .priority(dto.getPriority())
                .state(dto.getState() != null ? dto.getState() : TicketState.OPEN)
                .project(project)
                .build();
    }

    public Ticket toEntity(TicketCreateDto dto, Project project) {
        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setType(dto.getType());
        ticket.setPriority(dto.getPriority());
        ticket.setState(TicketState.OPEN); // defaultní stav OPEN
        ticket.setProject(project);
        return ticket;
    }
}
