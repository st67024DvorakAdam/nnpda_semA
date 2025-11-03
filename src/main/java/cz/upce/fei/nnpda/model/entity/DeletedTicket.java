package cz.upce.fei.nnpda.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "deleted_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeletedTicket {

    @Id
    private Long ticketId;

    private LocalDateTime deletedAt;
}
