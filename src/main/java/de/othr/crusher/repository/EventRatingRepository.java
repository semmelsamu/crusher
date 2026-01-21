package de.othr.crusher.repository;

import de.othr.crusher.model.EventRatingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRatingRepository extends JpaRepository<EventRatingEntity, Long> {
  /**
   * Finds a rating for a given user and event, if it exists.
   *
   * @param userId the ID of the user
   * @param eventId the ID of the event
   * @return the rating entity, if found
   */
  Optional<EventRatingEntity> findByUserIdAndEventId(Long userId, Long eventId);

  /**
   * Finds all ratings for a given event.
   *
   * @param eventId the ID of the event
   * @return list of ratings for the event
   */
  List<EventRatingEntity> findByEventId(Long eventId);
}
