package de.othr.crusher.repository;

import de.othr.crusher.model.SessionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link SessionEntity} entities.
 */
@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

    /**
     * Finds all sessions for a given user, ordered by start time (most recent first).
     *
     * @param userId identifier of the user
     * @return list of sessions belonging to the user, sorted by start time descending
     */
    List<SessionEntity> findByUserIdOrderByStartedAtDesc(Long userId);

    /**
     * Finds the active (running) session for a given user, if one exists.
     *
     * @param userId identifier of the user
     * @return optional containing the active session, or empty if no active session exists
     */
    Optional<SessionEntity> findByUserIdAndEndedAtIsNull(Long userId);
}

