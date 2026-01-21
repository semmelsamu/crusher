package de.othr.crusher.repository;

import de.othr.crusher.model.EventEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
  /**
   * Finds all events for a specific gym.
   *
   * @param gymId the ID of the gym
   * @return list of events for the gym
   */
  List<EventEntity> findByGymId(Long gymId);

  /**
   * Finds all non-deleted events for a specific gym.
   *
   * @param gymId the ID of the gym
   * @return list of non-deleted events for the gym
   */
  List<EventEntity> findByGymIdAndDeletedFalse(Long gymId);

  /**
   * Finds a non-deleted event by its ID.
   *
   * @param id the event ID
   * @return optional containing the event if found and not deleted
   */
  Optional<EventEntity> findByIdAndDeletedFalse(Long id);
}
