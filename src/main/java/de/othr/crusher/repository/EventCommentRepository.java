package de.othr.crusher.repository;

import de.othr.crusher.model.EventCommentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCommentRepository extends JpaRepository<EventCommentEntity, Long> {
  /**
   * Finds all comments for a given event, ordered by creation date (newest first).
   *
   * @param eventId the ID of the event
   * @return list of comments for the event, ordered by creation date
   */
  List<EventCommentEntity> findByEventIdOrderByCreatedAtDesc(Long eventId);
}
