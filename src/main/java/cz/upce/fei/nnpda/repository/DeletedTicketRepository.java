package cz.upce.fei.nnpda.repository;

import cz.upce.fei.nnpda.model.entity.DeletedTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedTicketRepository extends JpaRepository<DeletedTicket, Long> {
}
