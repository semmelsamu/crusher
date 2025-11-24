package de.othr.crusher.repository;

import de.othr.crusher.model.GymEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymRepository extends JpaRepository<GymEntity, Long> {
    // custom queries
}
