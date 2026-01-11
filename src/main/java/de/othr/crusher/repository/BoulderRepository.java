package de.othr.crusher.repository;

import de.othr.crusher.model.BoulderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link BoulderEntity} entities.
 */
@Repository
public interface BoulderRepository extends JpaRepository<BoulderEntity, Long> {

    /**
     * Finds all boulders for the given sector.
     *
     * @param sectorId identifier of the sector
     * @return list of boulders belonging to the sector
     */
    List<BoulderEntity> findBySectorId(Long sectorId);

    /**
     * Finds all boulders for the given gym.
     *
     * @param gymId identifier of the gym
     * @return list of boulders belonging to the gym
     */
    List<BoulderEntity> findBySectorGymId(Long gymId);

    /**
     * Finds all boulders for the given gym filtered by grade IDs.
     *
     * @param gymId identifier of the gym
     * @param gradeIds list of grade identifiers
     * @return list of boulders belonging to the gym with matching grades
     */
    List<BoulderEntity> findBySectorGymIdAndGradeIdIn(Long gymId, List<Long> gradeIds);

    /**
     * Finds all boulders for the given sector filtered by grade IDs.
     *
     * @param sectorId identifier of the sector
     * @param gradeIds list of grade identifiers
     * @return list of boulders belonging to the sector with matching grades
     */
    List<BoulderEntity> findBySectorIdAndGradeIdIn(Long sectorId, List<Long> gradeIds);

    /**
     * Finds all unpublished boulders for the given sector.
     *
     * @param sectorId identifier of the sector
     * @return list of unpublished boulders belonging to the sector
     */
    List<BoulderEntity> findBySectorIdAndPublishedFalse(Long sectorId);
}

