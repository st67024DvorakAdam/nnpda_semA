package cz.upce.fei.nnpda.controller.elastic;

import cz.upce.fei.nnpda.model.elastic.TicketDocument;
import cz.upce.fei.nnpda.service.elastic.TicketElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elastic/tickets")
@RequiredArgsConstructor
public class TicketElasticsearchController {

    private final TicketElasticsearchService ticketService;

    @GetMapping
    public List<TicketDocument> findAll() {
        return ticketService.findAll();
    }

    @GetMapping("/{id}")
    public TicketDocument findById(@PathVariable Long id) {
        return ticketService.findById(id).orElse(null);
    }

    @GetMapping("/search/title")
    public List<TicketDocument> searchByTitle(@RequestParam String keyword) {
        return ticketService.searchByTitle(keyword);
    }

    @GetMapping("/filter/state")
    public List<TicketDocument> filterByState(@RequestParam String state) {
        return ticketService.findByState(state);
    }

    @GetMapping("/filter/priority")
    public List<TicketDocument> filterByPriority(@RequestParam String priority) {
        return ticketService.findByPriority(priority);
    }

    @GetMapping("/filter/type")
    public List<TicketDocument> filterByType(@RequestParam String type) {
        return ticketService.findByType(type);
    }

    @GetMapping("/filter/assignee")
    public List<TicketDocument> filterByAssignee(@RequestParam String username) {
        return ticketService.findByAssigneeUsername(username);
    }
}
