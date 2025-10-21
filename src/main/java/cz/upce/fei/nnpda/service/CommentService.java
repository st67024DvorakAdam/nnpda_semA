package cz.upce.fei.nnpda.service;

import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.model.entity.Comment;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import cz.upce.fei.nnpda.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getCommentsForProject(Project project) {
        return commentRepository.findByProject(project);
    }

    public List<Comment> getCommentsForTicket(Ticket ticket) {
        return commentRepository.findByTicket(ticket);
    }

    public Comment addCommentToProject(Project project, AppUser author, String text) {
        Comment comment = Comment.builder()
                .project(project)
                .author(author)
                .text(text)
                .createdAt(LocalDateTime.now())
                .build();
        return commentRepository.save(comment);
    }

    public Comment addCommentToTicket(Ticket ticket, AppUser author, String text) {
        Comment comment = Comment.builder()
                .ticket(ticket)
                .author(author)
                .text(text)
                .createdAt(LocalDateTime.now())
                .build();
        return commentRepository.save(comment);
    }

    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }
}
