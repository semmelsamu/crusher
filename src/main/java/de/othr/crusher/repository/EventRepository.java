package de.othr.crusher.repository;

import de.othr.crusher.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    /**
     * Finds all events for a specific gym.
     *
     * @param gymId the ID of the gym
     * @return list of events for the gym
     */
    List<EventEntity> findByGymId(Long gymId);
}
