package cz.upce.fei.nnpda.mapper;

import cz.upce.fei.nnpda.model.dto.comment.CommentDto;
import cz.upce.fei.nnpda.model.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final AppUserMapper appUserMapper;

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor() != null ? appUserMapper.toDto(comment.getAuthor()) : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
