package de.othr.crusher.repository;

import de.othr.crusher.model.BoulderEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Finds all boulders for the given sector with pagination.
     *
     * @param sectorId identifier of the sector
     * @param pageable pagination information
     * @return page of boulders belonging to the sector
     */
    Page<BoulderEntity> findBySectorId(Long sectorId, Pageable pageable);

    /**
     * Finds all boulders for the given gym.
     *
     * @param gymId identifier of the gym
     * @return list of boulders belonging to the gym
     */
    List<BoulderEntity> findBySectorGymId(Long gymId);

    /**
     * Finds all boulders for the given gym with pagination.
     *
     * @param gymId identifier of the gym
     * @param pageable pagination information
     * @return page of boulders belonging to the gym
     */
    Page<BoulderEntity> findBySectorGymId(Long gymId, Pageable pageable);

    /**
     * Finds all boulders for the given gym filtered by grade IDs.
     *
     * @param gymId identifier of the gym
     * @param gradeIds list of grade identifiers
     * @return list of boulders belonging to the gym with matching grades
     */
    List<BoulderEntity> findBySectorGymIdAndGradeIdIn(Long gymId, List<Long> gradeIds);

    /**
     * Finds all boulders for the given gym filtered by grade IDs with pagination.
     *
     * @param gymId identifier of the gym
     * @param gradeIds list of grade identifiers
     * @param pageable pagination information
     * @return page of boulders belonging to the gym with matching grades
     */
    Page<BoulderEntity> findBySectorGymIdAndGradeIdIn(Long gymId, List<Long> gradeIds, Pageable pageable);

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

    /**
     * Finds all boulders for the given sector filtered by grade IDs with pagination.
     *
     * @param sectorId identifier of the sector
     * @param gradeIds list of grade identifiers
     * @param pageable pagination information
     * @return page of boulders belonging to the sector with matching grades
     */
    Page<BoulderEntity> findBySectorIdAndGradeIdIn(Long sectorId, List<Long> gradeIds, Pageable pageable);

    /**
     * Finds all non-deleted boulders for the given sector.
     *
     * @param sectorId identifier of the sector
     * @return list of non-deleted boulders belonging to the sector
     */
    List<BoulderEntity> findBySectorIdAndDeletedFalse(Long sectorId);

    /**
     * Finds all non-deleted boulders for the given sector with pagination.
     *
     * @param sectorId identifier of the sector
     * @param pageable pagination information
     * @return page of non-deleted boulders belonging to the sector
     */
    Page<BoulderEntity> findBySectorIdAndDeletedFalse(Long sectorId, Pageable pageable);

    /**
     * Finds all non-deleted boulders for the given gym.
     *
     * @param gymId identifier of the gym
     * @return list of non-deleted boulders belonging to the gym
     */
    List<BoulderEntity> findBySectorGymIdAndDeletedFalse(Long gymId);

    /**
     * Finds all non-deleted boulders for the given gym with pagination.
     *
     * @param gymId identifier of the gym
     * @param pageable pagination information
     * @return page of non-deleted boulders belonging to the gym
     */
    Page<BoulderEntity> findBySectorGymIdAndDeletedFalse(Long gymId, Pageable pageable);

    /**
     * Finds all non-deleted boulders for the given gym filtered by grade IDs.
     *
     * @param gymId identifier of the gym
     * @param gradeIds list of grade identifiers
     * @return list of non-deleted boulders belonging to the gym with matching grades
     */
    List<BoulderEntity> findBySectorGymIdAndGradeIdInAndDeletedFalse(Long gymId, List<Long> gradeIds);

    /**
     * Finds all non-deleted boulders for the given gym filtered by grade IDs with pagination.
     *
     * @param gymId identifier of the gym
     * @param gradeIds list of grade identifiers
     * @param pageable pagination information
     * @return page of non-deleted boulders belonging to the gym with matching grades
     */
    Page<BoulderEntity> findBySectorGymIdAndGradeIdInAndDeletedFalse(Long gymId, List<Long> gradeIds, Pageable pageable);

    /**
     * Finds all non-deleted boulders for the given sector filtered by grade IDs.
     *
     * @param sectorId identifier of the sector
     * @param gradeIds list of grade identifiers
     * @return list of non-deleted boulders belonging to the sector with matching grades
     */
    List<BoulderEntity> findBySectorIdAndGradeIdInAndDeletedFalse(Long sectorId, List<Long> gradeIds);

    /**
     * Finds all non-deleted boulders for the given sector filtered by grade IDs with pagination.
     *
     * @param sectorId identifier of the sector
     * @param gradeIds list of grade identifiers
     * @param pageable pagination information
     * @return page of non-deleted boulders belonging to the sector with matching grades
     */
    Page<BoulderEntity> findBySectorIdAndGradeIdInAndDeletedFalse(Long sectorId, List<Long> gradeIds, Pageable pageable);

    /**
     * Finds all non-deleted boulders.
     *
     * @return list of non-deleted boulders
     */
    List<BoulderEntity> findByDeletedFalse();

    /**
     * Finds all non-deleted boulders with pagination.
     *
     * @param pageable pagination information
     * @return page of non-deleted boulders
     */
    Page<BoulderEntity> findByDeletedFalse(Pageable pageable);

    /**
     * Finds all non-deleted boulders by IDs with pagination.
     *
     * @param ids boulder identifiers
     * @param pageable pagination information
     * @return page of non-deleted boulders matching IDs
     */
    Page<BoulderEntity> findByIdInAndDeletedFalse(List<Long> ids, Pageable pageable);

    /**
     * Finds non-deleted boulders for a sector by IDs with pagination.
     *
     * @param sectorId identifier of the sector
     * @param ids boulder identifiers
     * @param pageable pagination information
     * @return page of non-deleted boulders for the sector matching IDs
     */
    Page<BoulderEntity> findBySectorIdAndIdInAndDeletedFalse(Long sectorId, List<Long> ids, Pageable pageable);

    /**
     * Finds non-deleted boulders for a sector by grade IDs and boulder IDs with pagination.
     *
     * @param sectorId identifier of the sector
     * @param gradeIds list of grade identifiers
     * @param ids boulder identifiers
     * @param pageable pagination information
     * @return page of non-deleted boulders for the sector matching grades and IDs
     */
    Page<BoulderEntity> findBySectorIdAndGradeIdInAndIdInAndDeletedFalse(
            Long sectorId,
            List<Long> gradeIds,
            List<Long> ids,
            Pageable pageable);

    /**
     * Finds non-deleted boulders for a gym by IDs with pagination.
     *
     * @param gymId identifier of the gym
     * @param ids boulder identifiers
     * @param pageable pagination information
     * @return page of non-deleted boulders for the gym matching IDs
     */
    Page<BoulderEntity> findBySectorGymIdAndIdInAndDeletedFalse(Long gymId, List<Long> ids, Pageable pageable);

    /**
     * Finds non-deleted boulders for a gym by grade IDs and boulder IDs with pagination.
     *
     * @param gymId identifier of the gym
     * @param gradeIds list of grade identifiers
     * @param ids boulder identifiers
     * @param pageable pagination information
     * @return page of non-deleted boulders for the gym matching grades and IDs
     */
    Page<BoulderEntity> findBySectorGymIdAndGradeIdInAndIdInAndDeletedFalse(
            Long gymId,
            List<Long> gradeIds,
            List<Long> ids,
            Pageable pageable);
    /**
     * Finds a non-deleted boulder by its ID.
     *
     * @param id the boulder ID
     * @return optional containing the boulder if found and not deleted
     */
    Optional<BoulderEntity> findByIdAndDeletedFalse(Long id);
}
