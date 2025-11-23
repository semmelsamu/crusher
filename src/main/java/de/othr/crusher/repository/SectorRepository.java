package de.othr.crusher.repository;

import de.othr.crusher.model.SectorEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link SectorEntity} entities.
 */
@Repository
public interface SectorRepository extends JpaRepository<SectorEntity, Long> {

    /**
     * Finds all sectors for the given gym.
     *
     * @param gymId identifier of the gym
     * @return list of sectors belonging to the gym
     */
    List<SectorEntity> findByGymId(Long gymId);
}
