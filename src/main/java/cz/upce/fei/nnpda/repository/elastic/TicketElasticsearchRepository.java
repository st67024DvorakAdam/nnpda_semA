package cz.upce.fei.nnpda.repository.elastic;

import cz.upce.fei.nnpda.model.elastic.TicketDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketElasticsearchRepository extends ElasticsearchRepository<TicketDocument, Long> {

    List<TicketDocument> findByState(String state);

    List<TicketDocument> findByPriority(String priority);

    List<TicketDocument> findByType(String type);

    List<TicketDocument> findByAssigneeUsername(String username);

    List<TicketDocument> findByTitleContainingIgnoreCase(String title);
}
