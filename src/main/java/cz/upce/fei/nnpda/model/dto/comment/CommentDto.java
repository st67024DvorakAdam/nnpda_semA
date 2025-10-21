package cz.upce.fei.nnpda.model.dto.comment;

import cz.upce.fei.nnpda.model.dto.auth.AppUserDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private AppUserDto author;      // autor komentáře
    private LocalDateTime createdAt;
}
