package cz.upce.fei.nnpda.controller;

import cz.upce.fei.nnpda.model.dto.ticket.TicketCreateDto;
import cz.upce.fei.nnpda.model.dto.ticket.TicketDto;
import cz.upce.fei.nnpda.model.dto.ticket.TicketPatchDto;
import cz.upce.fei.nnpda.model.dto.ticket.TicketUpdateDto;
import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import cz.upce.fei.nnpda.service.AuthService;
import cz.upce.fei.nnpda.service.ProjectService;
import cz.upce.fei.nnpda.service.TicketService;
import cz.upce.fei.nnpda.mapper.TicketMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final AuthService authService;
    private final ProjectService projectService;

    private AppUser getCurrentUser() {
        String username = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return authService.loadUserByUsername(username);
    }

    @GetMapping
    public List<TicketDto> getTickets(@PathVariable Long projectId) {
        return ticketService.getTicketsForProject(projectId, getCurrentUser())
                .stream()
                .map(ticketMapper::toDto)
                .toList();
    }

    @PostMapping
    public TicketDto createTicket(@PathVariable Long projectId,
                                  @RequestBody @Valid TicketCreateDto ticketCreateDto) {
        Project project = projectService.getProjectByIdAndOwner(projectId, getCurrentUser());
        Ticket ticket = ticketMapper.toEntity(ticketCreateDto, project); // vždy OPEN
        Ticket saved = ticketService.createTicket(projectId, ticket, getCurrentUser());
        return ticketMapper.toDto(saved);
    }

    @GetMapping("/{ticketId}")
    public TicketDto getTicket(@PathVariable Long projectId, @PathVariable Long ticketId) {
        Ticket ticket = ticketService.getTicket(projectId, ticketId, getCurrentUser());
        return ticketMapper.toDto(ticket);
    }

    @PutMapping("/{ticketId}")
    public TicketDto updateTicket(@PathVariable Long projectId,
                                  @PathVariable Long ticketId,
                                  @RequestBody @Valid TicketUpdateDto ticketDto) {
        Ticket ticket = ticketService.getTicket(projectId, ticketId, getCurrentUser());

        ticket.setTitle(ticketDto.getTitle());
        ticket.setType(ticketDto.getType());
        ticket.setPriority(ticketDto.getPriority());
        ticket.setState(ticketDto.getState());

        if (ticketDto.getAssigneeId() != null) {
            ticket.setAssignee(authService.loadUserById(ticketDto.getAssigneeId()));
        } else {
            ticket.setAssignee(null); // odřadit řešitele
        }

        Ticket updated = ticketService.updateTicket(projectId, ticketId, ticket, getCurrentUser());
        return ticketMapper.toDto(updated);
    }


    @PatchMapping("/{ticketId}")
    public TicketDto patchTicket(@PathVariable Long projectId,
                                 @PathVariable Long ticketId,
                                 @RequestBody @Valid TicketPatchDto ticketDto) {
        Ticket ticket = ticketService.getTicket(projectId, ticketId, getCurrentUser());

        if (ticketDto.getTitle() != null) ticket.setTitle(ticketDto.getTitle());
        if (ticketDto.getType() != null) ticket.setType(ticketDto.getType());
        if (ticketDto.getPriority() != null) ticket.setPriority(ticketDto.getPriority());
        if (ticketDto.getState() != null) ticket.setState(ticketDto.getState());
        if (ticketDto.getAssigneeId() != null) {
            ticket.setAssignee(authService.loadUserById(ticketDto.getAssigneeId()));
        } else if (ticketDto.getAssigneeId() != null && ticketDto.getAssigneeId() == 0) {
            // možnost odřadit řešitele
            ticket.setAssignee(null);
        }

        Ticket updated = ticketService.updateTicket(projectId, ticketId, ticket, getCurrentUser());
        return ticketMapper.toDto(updated);
    }


    @DeleteMapping("/{ticketId}")
    public void deleteTicket(@PathVariable Long projectId, @PathVariable Long ticketId) {
        ticketService.deleteTicket(projectId, ticketId, getCurrentUser());
    }
}
