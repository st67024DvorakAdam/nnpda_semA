package cz.upce.fei.nnpda.model.elastic;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "tickets-main")
public class TicketDocument {
    @Id
    private Long id;
    private String title;
    private String state;
    private String priority;
    private String type;
    private Long projectId;
    private String projectName;
    private Long assigneeId;
    private String assigneeUsername;
}
