package de.othr.crusher.repository;

import de.othr.crusher.model.GradeEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for managing {@link GradeEntity} entities. */
@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, Long> {

  /**
   * Finds all grades for the given gym.
   *
   * @param gymId identifier of the gym
   * @return list of grades belonging to the gym
   */
  List<GradeEntity> findByGymId(Long gymId);

  /**
   * Finds all non-deleted grades for the given gym.
   *
   * @param gymId identifier of the gym
   * @return list of non-deleted grades belonging to the gym
   */
  List<GradeEntity> findByGymIdAndDeletedFalse(Long gymId);

  /**
   * Finds all non-deleted grades.
   *
   * @return list of non-deleted grades
   */
  List<GradeEntity> findByDeletedFalse();

  /**
   * Finds a non-deleted grade by its ID.
   *
   * @param id the grade ID
   * @return optional containing the grade if found and not deleted
   */
  Optional<GradeEntity> findByIdAndDeletedFalse(Long id);
}
