package cz.upce.fei.nnpda.repository;

import cz.upce.fei.nnpda.model.entity.Comment;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProject(Project project);
    List<Comment> findByTicket(Ticket ticket);
}
