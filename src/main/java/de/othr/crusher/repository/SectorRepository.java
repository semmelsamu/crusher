package de.othr.crusher.repository;

import de.othr.crusher.model.SectorEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for managing {@link SectorEntity} entities. */
@Repository
public interface SectorRepository extends JpaRepository<SectorEntity, Long> {

  /**
   * Finds all sectors for the given gym.
   *
   * @param gymId identifier of the gym
   * @return list of sectors belonging to the gym
   */
  List<SectorEntity> findByGymId(Long gymId);

  /**
   * Finds all non-deleted sectors for the given gym.
   *
   * @param gymId identifier of the gym
   * @return list of non-deleted sectors belonging to the gym
   */
  List<SectorEntity> findByGymIdAndDeletedFalse(Long gymId);

  /**
   * Finds all non-deleted sectors.
   *
   * @return list of non-deleted sectors
   */
  List<SectorEntity> findByDeletedFalse();

  /**
   * Finds a non-deleted sector by its ID.
   *
   * @param id the sector ID
   * @return optional containing the sector if found and not deleted
   */
  Optional<SectorEntity> findByIdAndDeletedFalse(Long id);
}
