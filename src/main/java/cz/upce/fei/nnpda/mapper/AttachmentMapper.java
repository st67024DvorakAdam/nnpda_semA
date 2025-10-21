package cz.upce.fei.nnpda.mapper;

import cz.upce.fei.nnpda.model.dto.attachment.AttachmentDto;
import cz.upce.fei.nnpda.model.entity.Attachment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttachmentMapper {

    private final AppUserMapper appUserMapper;

    public AttachmentDto toDto(Attachment attachment) {
        return AttachmentDto.builder()
                .id(attachment.getId())
                .filename(attachment.getFilename())
                .contentType(attachment.getContentType())
                .size(attachment.getSize())
                .filePath(attachment.getFilePath())
                .uploader(attachment.getUploader() != null ? appUserMapper.toDto(attachment.getUploader()) : null)
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }
}
