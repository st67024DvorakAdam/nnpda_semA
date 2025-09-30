package cz.upce.fei.nnpda.controller;

import cz.upce.fei.nnpda.model.dto.ticket.TicketDto;
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
                                  @RequestBody @Valid TicketDto ticketDto) {
        Project project = projectService.getProjectByIdAndOwner(projectId, getCurrentUser());
        Ticket ticket = ticketMapper.toEntity(ticketDto, project);
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
                                  @RequestBody @Valid TicketDto ticketDto) {
        Project project = projectService.getProjectByIdAndOwner(projectId, getCurrentUser());
        Ticket updatedEntity = ticketMapper.toEntity(ticketDto, project);
        Ticket updated = ticketService.updateTicket(projectId, ticketId, updatedEntity, getCurrentUser());
        return ticketMapper.toDto(updated);
    }

    @PatchMapping("/{ticketId}")
    public TicketDto patchTicket(@PathVariable Long projectId,
                                 @PathVariable Long ticketId,
                                 @RequestBody TicketDto ticketDto) {
        Project project = projectService.getProjectByIdAndOwner(projectId, getCurrentUser());
        Ticket ticket = ticketService.getTicket(projectId, ticketId, getCurrentUser());

        if (ticketDto.getTitle() != null) ticket.setTitle(ticketDto.getTitle());
        if (ticketDto.getType() != null) ticket.setType(ticketDto.getType());
        if (ticketDto.getPriority() != null) ticket.setPriority(ticketDto.getPriority());
        if (ticketDto.getState() != null) ticket.setState(ticketDto.getState());

        Ticket updated = ticketService.updateTicket(projectId, ticketId, ticket, getCurrentUser());
        return ticketMapper.toDto(updated);
    }


    @DeleteMapping("/{ticketId}")
    public void deleteTicket(@PathVariable Long projectId, @PathVariable Long ticketId) {
        ticketService.deleteTicket(projectId, ticketId, getCurrentUser());
    }
}
