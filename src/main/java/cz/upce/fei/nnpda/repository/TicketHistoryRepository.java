package cz.upce.fei.nnpda.repository;

import cz.upce.fei.nnpda.model.entity.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {

}
