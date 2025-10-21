package cz.upce.fei.nnpda.service;

import cz.upce.fei.nnpda.model.entity.AppUser;
import cz.upce.fei.nnpda.model.entity.Attachment;
import cz.upce.fei.nnpda.model.entity.Project;
import cz.upce.fei.nnpda.model.entity.Ticket;
import cz.upce.fei.nnpda.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    public List<Attachment> getAttachmentsForProject(Project project) {
        return attachmentRepository.findByProject(project);
    }

    public List<Attachment> getAttachmentsForTicket(Ticket ticket) {
        return attachmentRepository.findByTicket(ticket);
    }

    private Attachment addAttachmentToProject(Project project, AppUser uploader,
                                             String filename, String filePath, String contentType, Long size) {
        Attachment attachment = Attachment.builder()
                .project(project)
                .uploader(uploader)
                .filename(filename)
                .filePath(filePath)
                .contentType(contentType)
                .size(size)
                .uploadedAt(LocalDateTime.now())
                .build();
        return attachmentRepository.save(attachment);
    }

    private Attachment addAttachmentToTicket(Ticket ticket, AppUser uploader,
                                            String filename, String filePath, String contentType, Long size) {
        Attachment attachment = Attachment.builder()
                .ticket(ticket)
                .uploader(uploader)
                .filename(filename)
                .filePath(filePath)
                .contentType(contentType)
                .size(size)
                .uploadedAt(LocalDateTime.now())
                .build();
        return attachmentRepository.save(attachment);
    }

    public void deleteAttachment(Attachment attachment) {
        try {
            Path path = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        attachmentRepository.delete(attachment);
    }

    public Attachment uploadFileForProject(Project project, AppUser uploader, MultipartFile file) {
        try {
            String uploadDir = "uploads/projects/" + project.getId();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = Paths.get(file.getOriginalFilename()).getFileName().toString();
            Path filePath = uploadPath.resolve(filename);
            System.out.println("Ukládám soubor na: " + filePath.toAbsolutePath());

            Files.copy(file.getInputStream(), filePath);

            return addAttachmentToProject(project, uploader, filename, filePath.toString(), file.getContentType(), file.getSize());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    public Attachment uploadFileForTicket(Ticket ticket, AppUser uploader, MultipartFile file) {
        try {
            String uploadDir = "uploads/tickets/" + ticket.getId();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = Paths.get(file.getOriginalFilename()).getFileName().toString();
            Path filePath = uploadPath.resolve(filename);
            System.out.println("Ukládám soubor na: " + filePath.toAbsolutePath());

            Files.copy(file.getInputStream(), filePath);

            return addAttachmentToTicket(ticket, uploader, filename, filePath.toString(), file.getContentType(), file.getSize());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    public Attachment getAttachmentById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found with id " + attachmentId));
    }

}
