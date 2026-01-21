package de.othr.crusher.repository;

import de.othr.crusher.model.SessionEntity;
import de.othr.crusher.model.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repository for managing {@link SessionEntity} entities. */
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
   * Finds sessions for a given user with pagination, ordered by start time (most recent first).
   *
   * @param userId identifier of the user
   * @param pageable pagination information
   * @return page of sessions belonging to the user, sorted by start time descending
   */
  Page<SessionEntity> findByUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);

  /**
   * Finds the active (running) session for a given user, if one exists. Returns the most recently
   * started session if multiple active sessions exist.
   *
   * @param userId identifier of the user
   * @return optional containing the active session, or empty if no active session exists
   */
  Optional<SessionEntity> findFirstByUserIdAndEndedAtIsNullOrderByStartedAtDesc(Long userId);

  /**
   * Finds all distinct users who have ever had a session at the given gym.
   *
   * @param gymId identifier of the gym
   * @return list of unique users who have had sessions at this gym
   */
  @Query("SELECT DISTINCT s.user FROM SessionEntity s WHERE s.gym.id = :gymId")
  List<UserEntity> findDistinctUsersByGymId(@Param("gymId") Long gymId);
}
