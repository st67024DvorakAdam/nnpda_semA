package cz.upce.fei.nnpda.repository;

import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByProject(Project project);

    Optional<Ticket> findByIdAndProject(Long id, Project project);
}
