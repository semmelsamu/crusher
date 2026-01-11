package de.othr.crusher.repository;

import de.othr.crusher.model.GymEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymRepository extends JpaRepository<GymEntity, Long> {

    /**
     * Finds all non-deleted gyms.
     *
     * @return list of non-deleted gyms
     */
    List<GymEntity> findByDeletedFalse();
}
