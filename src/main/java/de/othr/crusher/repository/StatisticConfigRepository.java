package de.othr.crusher.repository;

import de.othr.crusher.model.StatisticConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing {@link StatisticConfigEntity} entities.
 */
@Repository
public interface StatisticConfigRepository extends JpaRepository<StatisticConfigEntity, Long> {

    /**
     * Finds the statistic configuration for a given user.
     *
     * @param userId identifier of the user
     * @return optional containing the config if found
     */
    Optional<StatisticConfigEntity> findByUserId(Long userId);
}
