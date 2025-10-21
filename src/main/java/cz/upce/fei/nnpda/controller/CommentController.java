package cz.upce.fei.nnpda.controller;

import cz.upce.fei.nnpda.model.dto.comment.CommentCreateDto;
import cz.upce.fei.nnpda.model.dto.comment.CommentDto;
import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import cz.upce.fei.nnpda.model.entity.Comment;
import cz.upce.fei.nnpda.service.AuthService;
import cz.upce.fei.nnpda.service.CommentService;
import cz.upce.fei.nnpda.service.ProjectService;
import cz.upce.fei.nnpda.service.TicketService;
import cz.upce.fei.nnpda.mapper.CommentMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final AuthService authService;
    private final ProjectService projectService;
    private final TicketService ticketService;

    private AppUser getCurrentUser() {
        String username = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return authService.loadUserByUsername(username);
    }

    // ----------------- Komentáře k projektu -----------------
    @GetMapping("/projects/{projectId}/comments")
    public List<CommentDto> getProjectComments(@PathVariable Long projectId) {
        Project project = projectService.getProjectById(projectId);
        AppUser currentUser = getCurrentUser();
        if (!project.getOwner().equals(currentUser)) {
            throw new RuntimeException("Access denied");
        }
        return commentService.getCommentsForProject(project)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @PostMapping("/projects/{projectId}/comments")
    public CommentDto createProjectComment(@PathVariable Long projectId,
                                           @RequestBody @Valid CommentCreateDto commentDto) {
        Project project = projectService.getProjectById(projectId);
        Comment comment = commentService.addCommentToProject(project, getCurrentUser(), commentDto.getText());
        return commentMapper.toDto(comment);
    }

    @DeleteMapping("/projects/{projectId}/comments/{commentId}")
    public void deleteProjectComment(@PathVariable Long projectId, @PathVariable Long commentId) {
        Comment comment = commentService.getCommentsForProject(projectService.getProjectById(projectId))
                .stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthor().equals(getCurrentUser())) {
            throw new RuntimeException("Access denied");
        }

        commentService.deleteComment(comment);
    }

    // ----------------- Komentáře k ticketu -----------------
    @GetMapping("/projects/{projectId}/tickets/{ticketId}/comments")
    public List<CommentDto> getTicketComments(@PathVariable Long projectId, @PathVariable Long ticketId) {
        Ticket ticket = ticketService.getTicket(projectId, ticketId, getCurrentUser());
        return commentService.getCommentsForTicket(ticket)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @PostMapping("/projects/{projectId}/tickets/{ticketId}/comments")
    public CommentDto createTicketComment(@PathVariable Long projectId, @PathVariable Long ticketId,
                                          @RequestBody @Valid CommentCreateDto commentDto) {
        Ticket ticket = ticketService.getTicket(projectId, ticketId, getCurrentUser());
        Comment comment = commentService.addCommentToTicket(ticket, getCurrentUser(), commentDto.getText());
        return commentMapper.toDto(comment);
    }

    @DeleteMapping("/projects/{projectId}/tickets/{ticketId}/comments/{commentId}")
    public void deleteTicketComment(@PathVariable Long projectId, @PathVariable Long ticketId,
                                    @PathVariable Long commentId) {
        Ticket ticket = ticketService.getTicket(projectId, ticketId, getCurrentUser());
        Comment comment = commentService.getCommentsForTicket(ticket)
                .stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthor().equals(getCurrentUser())) {
            throw new RuntimeException("Access denied");
        }

        commentService.deleteComment(comment);
    }
}
