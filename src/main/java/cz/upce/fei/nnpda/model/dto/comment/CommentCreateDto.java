package cz.upce.fei.nnpda.model.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCreateDto {
    private String text;
}
