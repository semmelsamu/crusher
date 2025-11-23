package de.othr.crusher.repository;

import de.othr.crusher.model.Gym;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymRepository extends JpaRepository<Gym, Long> {
    // custom queries
}
