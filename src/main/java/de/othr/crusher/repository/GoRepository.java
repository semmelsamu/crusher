package de.othr.crusher.repository;

import de.othr.crusher.model.GoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link GoEntity} entities.
 */
@Repository
public interface GoRepository extends JpaRepository<GoEntity, Long> {

    /**
     * Finds all goes for a given session, ordered by timestamp (most recent first).
     *
     * @param sessionId identifier of the session
     * @return list of goes belonging to the session, sorted by timestamp descending
     */
    List<GoEntity> findBySessionIdOrderByTimestampDesc(Long sessionId);

    /**
     * Finds all goes for a given boulder, ordered by timestamp (most recent first).
     *
     * @param boulderId identifier of the boulder
     * @return list of goes for the boulder, sorted by timestamp descending
     */
    List<GoEntity> findByBoulderIdOrderByTimestampDesc(Long boulderId);

    /**
     * Finds all goes for a given user across all sessions.
     *
     * @param userId identifier of the user
     * @return list of all goes belonging to the user
     */
    List<GoEntity> findBySession_UserId(Long userId);
}
