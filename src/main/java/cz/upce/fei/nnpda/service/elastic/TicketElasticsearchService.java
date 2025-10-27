package cz.upce.fei.nnpda.service.elastic;

import cz.upce.fei.nnpda.model.elastic.TicketDocument;
import cz.upce.fei.nnpda.repository.elastic.TicketElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TicketElasticsearchService {

    private final TicketElasticsearchRepository ticketRepository;

    public List<TicketDocument> findAll() {
        return StreamSupport.stream(ticketRepository.findAll().spliterator(), false)
                .toList();
    }

    public Optional<TicketDocument> findById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<TicketDocument> findByState(String state) {
        return ticketRepository.findByState(state);
    }

    public List<TicketDocument> findByPriority(String priority) {
        return ticketRepository.findByPriority(priority);
    }

    public List<TicketDocument> findByType(String type) {
        return ticketRepository.findByType(type);
    }

    public List<TicketDocument> findByAssigneeUsername(String username) {
        return ticketRepository.findByAssigneeUsername(username);
    }

    // fulltext search
    public List<TicketDocument> searchByTitle(String keyword) {
        return ticketRepository.findByTitleContainingIgnoreCase(keyword);
    }
}
