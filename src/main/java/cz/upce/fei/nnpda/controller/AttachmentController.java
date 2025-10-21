package cz.upce.fei.nnpda.controller;

import cz.upce.fei.nnpda.model.dto.attachment.AttachmentDto;
import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import cz.upce.fei.nnpda.model.entity.Attachment;
import cz.upce.fei.nnpda.service.AuthService;
import cz.upce.fei.nnpda.service.AttachmentService;
import cz.upce.fei.nnpda.service.ProjectService;
import cz.upce.fei.nnpda.service.TicketService;
import cz.upce.fei.nnpda.mapper.AttachmentMapper;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final AttachmentMapper attachmentMapper;
    private final AuthService authService;
    private final ProjectService projectService;
    private final TicketService ticketService;

    private AppUser getCurrentUser() {
        String username = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return authService.loadUserByUsername(username);
    }

    // ----------------- Přílohy k projektu -----------------
    @GetMapping("/projects/{projectId}/attachments")
    public List<AttachmentDto> getProjectAttachments(@PathVariable Long projectId) {
        Project project = projectService.getProjectById(projectId);
        return attachmentService.getAttachmentsForProject(project)
                .stream()
                .map(attachmentMapper::toDto)
                .toList();
    }

    @PostMapping(value = "/projects/{projectId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentDto uploadProjectAttachment(@PathVariable Long projectId,
                                                 @RequestParam("file") MultipartFile file) {
        Project project = projectService.getProjectById(projectId);
        Attachment attachment = attachmentService.uploadFileForProject(project, getCurrentUser(), file);
        return attachmentMapper.toDto(attachment);
    }

    @DeleteMapping("/projects/{projectId}/attachments/{attachmentId}")
    public void deleteProjectAttachment(@PathVariable Long projectId, @PathVariable Long attachmentId) {
        Project project = projectService.getProjectById(projectId);
        Attachment attachment = attachmentService.getAttachmentsForProject(project)
                .stream()
                .filter(a -> a.getId().equals(attachmentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        if (!attachment.getUploader().equals(getCurrentUser())) {
            throw new RuntimeException("Access denied");
        }

        attachmentService.deleteAttachment(attachment);
    }

    // ----------------- Přílohy k ticketu -----------------
    @GetMapping("/projects/{projectId}/tickets/{ticketId}/attachments")
    public List<AttachmentDto> getTicketAttachments(@PathVariable Long projectId, @PathVariable Long ticketId) {
        Ticket ticket = ticketService.getTicket(projectId, ticketId, getCurrentUser());
        return attachmentService.getAttachmentsForTicket(ticket)
                .stream()
                .map(attachmentMapper::toDto)
                .toList();
    }

    @PostMapping(value = "/projects/{projectId}/tickets/{ticketId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentDto uploadTicketAttachment(@PathVariable Long projectId, @PathVariable Long ticketId,
                                                @RequestParam("file") MultipartFile file) {
        Ticket ticket = ticketService.getTicket(projectId, ticketId, getCurrentUser());
        Attachment attachment = attachmentService.uploadFileForTicket(ticket, getCurrentUser(), file);
        return attachmentMapper.toDto(attachment);
    }

    @DeleteMapping("/projects/{projectId}/tickets/{ticketId}/attachments/{attachmentId}")
    public void deleteTicketAttachment(@PathVariable Long projectId, @PathVariable Long ticketId,
                                       @PathVariable Long attachmentId) {
        Ticket ticket = ticketService.getTicket(projectId, ticketId, getCurrentUser());
        Attachment attachment = attachmentService.getAttachmentsForTicket(ticket)
                .stream()
                .filter(a -> a.getId().equals(attachmentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        if (!attachment.getUploader().equals(getCurrentUser())) {
            throw new RuntimeException("Access denied");
        }

        attachmentService.deleteAttachment(attachment);
    }

    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) throws IOException {
        Attachment attachment = attachmentService.getAttachmentById(attachmentId);

        Path path = Paths.get(attachment.getFilePath());
        Resource resource = null;
        try {
            resource = new org.springframework.core.io.UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Could not read file: " + attachment.getFilename());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFilename() + "\"")
                .body(resource);
    }

}
