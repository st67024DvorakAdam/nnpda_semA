package cz.upce.fei.nnpda.service;

import cz.upce.fei.nnpda.model.entity.TicketHistory;
import cz.upce.fei.nnpda.repository.TicketHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketHistoryService {

    private final TicketHistoryRepository ticketHistoryRepository;

    public void save(TicketHistory history) {
        ticketHistoryRepository.save(history);
    }
}
