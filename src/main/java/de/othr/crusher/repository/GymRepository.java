package de.othr.crusher.repository;

import de.othr.crusher.model.GymEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GymRepository extends JpaRepository<GymEntity, Long> {

    /**
     * Finds all non-deleted gyms.
     *
     * @return list of non-deleted gyms
     */
    List<GymEntity> findByDeletedFalse();

    /**
     * Finds a non-deleted gym by its ID.
     *
     * @param id the gym ID
     * @return optional containing the gym if found and not deleted
     */
    Optional<GymEntity> findByIdAndDeletedFalse(Long id);
}
