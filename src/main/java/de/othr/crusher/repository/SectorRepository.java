package de.othr.crusher.repository;

import de.othr.crusher.model.Sector;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Sector} entities.
 */
@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {

    /**
     * Finds all sectors for the given gym.
     *
     * @param gymId identifier of the gym
     * @return list of sectors belonging to the gym
     */
    List<Sector> findByGymId(Long gymId);
}
