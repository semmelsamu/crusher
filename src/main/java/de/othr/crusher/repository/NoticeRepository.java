package de.othr.crusher.repository;

import de.othr.crusher.model.NoticeEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {
  /**
   * Finds all notices for a specific gym.
   *
   * @param gymId the ID of the gym
   * @return list of notices for the gym
   */
  List<NoticeEntity> findByGymId(Long gymId);

  /**
   * Finds all notices for a specific gym, ordered by creation date descending (newest first).
   *
   * @param gymId the ID of the gym
   * @return list of notices for the gym, sorted by creation date descending
   */
  List<NoticeEntity> findByGymIdOrderByCreationDateDesc(Long gymId);

  /**
   * Finds all non-deleted notices for a specific gym.
   *
   * @param gymId the ID of the gym
   * @return list of non-deleted notices for the gym
   */
  List<NoticeEntity> findByGymIdAndDeletedFalse(Long gymId);

  /**
   * Finds all non-deleted notices for a specific gym, ordered by creation date descending.
   *
   * @param gymId the ID of the gym
   * @return list of non-deleted notices for the gym, sorted by creation date descending
   */
  List<NoticeEntity> findByGymIdAndDeletedFalseOrderByCreationDateDesc(Long gymId);

  /**
   * Finds a non-deleted notice by its ID.
   *
   * @param id the notice ID
   * @return optional containing the notice if found and not deleted
   */
  Optional<NoticeEntity> findByIdAndDeletedFalse(Long id);
}
