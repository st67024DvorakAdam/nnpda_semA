package cz.upce.fei.nnpda.service;

import cz.upce.fei.nnpda.exception.project.ForbiddenException;
import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import cz.upce.fei.nnpda.model.entity.TicketHistory;
import cz.upce.fei.nnpda.repository.ProjectRepository;
import cz.upce.fei.nnpda.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final TicketHistoryService ticketHistoryService;
    private final DeletedTicketService deletedTicketService;

    public List<Ticket> getTicketsForProject(Long projectId, AppUser currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        if (!project.getOwner().equals(currentUser)) {
            throw new ForbiddenException("Access to foreign project is forbidden");
        }

        return ticketRepository.findByProject(project);
    }

    public Ticket createTicket(Long projectId, Ticket ticket, AppUser currentUser) {
        Project project = projectRepository.findByIdAndOwner(projectId, currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        ticket.setProject(project);
        Ticket savedTicket = ticketRepository.save(ticket);

        // vytvoření prvního záznamu historie
        TicketHistory history = TicketHistory.builder()
                .ticket(savedTicket)
                .state(savedTicket.getState())
                .priority(savedTicket.getPriority())
                .assignee(savedTicket.getAssignee())
                .changeTime(java.time.LocalDateTime.now())
                .build();

        ticketHistoryService.save(history);

        return savedTicket;
    }

    public Ticket getTicket(Long projectId, Long ticketId, AppUser currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        if (!project.getOwner().equals(currentUser)) {
            throw new ForbiddenException("Access to foreign project is forbidden");
        }

        return ticketRepository.findByIdAndProject(ticketId, project)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
    }


    public Ticket updateTicket(Long projectId, Long ticketId, Ticket updatedTicket, AppUser currentUser) {
        Ticket ticket = getTicket(projectId, ticketId, currentUser);

        // vytvoření historie před změnou
        TicketHistory history = TicketHistory.builder()
                .ticket(ticket)
                .state(ticket.getState())
                .priority(ticket.getPriority())
                .assignee(ticket.getAssignee())
                .changeTime(java.time.LocalDateTime.now())
                .build();
        ticketHistoryService.save(history);

        ticket.setTitle(updatedTicket.getTitle());
        ticket.setType(updatedTicket.getType());
        ticket.setPriority(updatedTicket.getPriority());
        ticket.setState(updatedTicket.getState());
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Long projectId, Long ticketId, AppUser currentUser) {
        Ticket ticket = getTicket(projectId, ticketId, currentUser);
        deletedTicketService.logDeletedTicket(ticket.getId());
        ticketRepository.delete(ticket);
    }
}
