package cz.upce.fei.nnpda.service;

import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
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

    public List<Ticket> getTicketsForProject(Long projectId, AppUser currentUser) {
        Project project = projectRepository.findByIdAndOwner(projectId, currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        return ticketRepository.findByProject(project);
    }

    public Ticket createTicket(Long projectId, Ticket ticket, AppUser currentUser) {
        Project project = projectRepository.findByIdAndOwner(projectId, currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        ticket.setProject(project);
        return ticketRepository.save(ticket);
    }

    public Ticket getTicket(Long projectId, Long ticketId, AppUser currentUser) {
        Project project = projectRepository.findByIdAndOwner(projectId, currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        return ticketRepository.findByIdAndProject(ticketId, project)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
    }

    public Ticket updateTicket(Long projectId, Long ticketId, Ticket updatedTicket, AppUser currentUser) {
        Ticket ticket = getTicket(projectId, ticketId, currentUser);
        ticket.setTitle(updatedTicket.getTitle());
        ticket.setType(updatedTicket.getType());
        ticket.setPriority(updatedTicket.getPriority());
        ticket.setState(updatedTicket.getState());
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Long projectId, Long ticketId, AppUser currentUser) {
        Ticket ticket = getTicket(projectId, ticketId, currentUser);
        ticketRepository.delete(ticket);
    }
}
