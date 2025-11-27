package de.othr.crusher.repository;

import de.othr.crusher.model.GradeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link GradeEntity} entities.
 */
@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, Long> {

    /**
     * Finds all grades for the given gym.
     *
     * @param gymId identifier of the gym
     * @return list of grades belonging to the gym
     */
    List<GradeEntity> findByGymId(Long gymId);
}
