package de.othr.crusher.repository;

import de.othr.crusher.model.GymRatingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for managing {@link GymRatingEntity} entries that link users to gym ratings. */
@Repository
public interface GymRatingRepository extends JpaRepository<GymRatingEntity, Long> {

  /**
   * Finds a specific rating entry for a user and gym.
   *
   * @param userId identifier of the user
   * @param gymId identifier of the gym
   * @return optional containing the rating if present
   */
  Optional<GymRatingEntity> findByUserIdAndGymId(Long userId, Long gymId);

  /**
   * Finds all ratings for a given user.
   *
   * @param userId identifier of the user
   * @return list of ratings for the user
   */
  List<GymRatingEntity> findByUserId(Long userId);

  /**
   * Finds all ratings for a given gym.
   *
   * @param gymId identifier of the gym
   * @return list of ratings for the gym
   */
  List<GymRatingEntity> findByGymId(Long gymId);
}
