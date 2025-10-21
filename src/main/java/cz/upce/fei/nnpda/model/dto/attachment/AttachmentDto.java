package cz.upce.fei.nnpda.model.dto.attachment;

import cz.upce.fei.nnpda.model.dto.auth.AppUserDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttachmentDto {
    private Long id;
    private String filename;
    private String contentType;
    private Long size;
    private String filePath;        // relativní cesta na serveru
    private AppUserDto uploader;
    private LocalDateTime uploadedAt;
}
