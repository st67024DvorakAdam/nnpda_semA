package cz.upce.fei.nnpda.service;

import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import cz.upce.fei.nnpda.model.entity.TicketHistory;
import cz.upce.fei.nnpda.model.entity.enums.ProjectStatus;
import cz.upce.fei.nnpda.model.entity.enums.TicketPriority;
import cz.upce.fei.nnpda.model.entity.enums.TicketState;
import cz.upce.fei.nnpda.model.entity.enums.TicketType;
import cz.upce.fei.nnpda.repository.AppUserRepository;
import cz.upce.fei.nnpda.repository.ProjectRepository;
import cz.upce.fei.nnpda.repository.TicketHistoryRepository;
import cz.upce.fei.nnpda.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeneratorService {

    private final AppUserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TicketRepository ticketRepository;
    private final TicketHistoryRepository historyRepository;

    private final Random random = new Random();

    // počet projektů a počet tiketů je pevně nastavený
    private final int PROJECT_COUNT = 10;
    private final int MIN_TICKETS = 10;
    private final int MAX_TICKETS = 100;

    public void generateProjectsWithTickets() {
        List<AppUser> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new RuntimeException("No users found in DB");
        }

        for (int i = 0; i < PROJECT_COUNT; i++) {
            // vyber náhodného vlastníka projektu
            AppUser owner = users.get(random.nextInt(users.size()));

            Project project = Project.builder()
                    .name("Project " + UUID.randomUUID())
                    .description("Auto-generated project")
                    .status(ProjectStatus.ACTIVE)
                    .owner(owner)
                    .build();
            projectRepository.save(project);

            // generování tiketů
            int ticketCount = MIN_TICKETS + random.nextInt(MAX_TICKETS - MIN_TICKETS + 1);
            for (int j = 0; j < ticketCount; j++) {
                Ticket ticket = Ticket.builder()
                        .title("Ticket " + UUID.randomUUID())
                        .type(TicketType.values()[random.nextInt(TicketType.values().length)])
                        .priority(TicketPriority.values()[random.nextInt(TicketPriority.values().length)])
                        .state(TicketState.values()[random.nextInt(TicketState.values().length)])
                        .project(project)
                        .assignee(users.get(random.nextInt(users.size())))
                        .build();

                ticketRepository.save(ticket);

                // vytvoření první historie ticketu
                TicketHistory history = TicketHistory.builder()
                        .ticket(ticket)
                        .state(ticket.getState())
                        .priority(ticket.getPriority())
                        .assignee(ticket.getAssignee())
                        .changeTime(LocalDateTime.now())
                        .build();
                historyRepository.save(history);
            }
        }
    }
}

