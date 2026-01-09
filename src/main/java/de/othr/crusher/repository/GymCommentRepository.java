package de.othr.crusher.repository;

import de.othr.crusher.model.GymCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing {@link GymCommentEntity} entries that link users to gym comments.
 */
@Repository
public interface GymCommentRepository extends JpaRepository<GymCommentEntity, Long> {

    /**
     * Finds all comments for a given gym, ordered by creation date (newest first).
     *
     * @param gymId identifier of the gym
     * @return list of comments for the gym, ordered by creation date
     */
    List<GymCommentEntity> findByGymIdOrderByCreatedAtDesc(Long gymId);
}
