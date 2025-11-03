package cz.upce.fei.nnpda.service;

import cz.upce.fei.nnpda.model.entity.DeletedTicket;
import cz.upce.fei.nnpda.repository.DeletedTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeletedTicketService {

    private final DeletedTicketRepository deletedTicketRepository;

    public void logDeletedTicket(Long ticketId) {
        DeletedTicket dt = DeletedTicket.builder()
                .ticketId(ticketId)
                .deletedAt(LocalDateTime.now())
                .build();
        deletedTicketRepository.save(dt);
    }
}
