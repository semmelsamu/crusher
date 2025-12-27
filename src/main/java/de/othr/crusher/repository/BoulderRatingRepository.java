package de.othr.crusher.repository;

import de.othr.crusher.model.BoulderRatingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link BoulderRatingEntity} entries that link users to boulder ratings.
 */
@Repository
public interface BoulderRatingRepository extends JpaRepository<BoulderRatingEntity, Long> {

    /**
     * Finds a specific rating entry for a user and boulder.
     *
     * @param userId identifier of the user
     * @param boulderId identifier of the boulder
     * @return optional containing the rating if present
     */
    Optional<BoulderRatingEntity> findByUserIdAndBoulderId(Long userId, Long boulderId);

    /**
     * Finds all ratings for a given user.
     *
     * @param userId identifier of the user
     * @return list of ratings for the user
     */
    List<BoulderRatingEntity> findByUserId(Long userId);
}
