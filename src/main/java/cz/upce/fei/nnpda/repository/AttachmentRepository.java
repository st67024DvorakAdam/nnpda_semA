package cz.upce.fei.nnpda.repository;

import cz.upce.fei.nnpda.model.entity.Attachment;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByProject(Project project);
    List<Attachment> findByTicket(Ticket ticket);
}
