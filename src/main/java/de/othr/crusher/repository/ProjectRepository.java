package de.othr.crusher.repository;

import de.othr.crusher.model.ProjectEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link ProjectEntity} entries that link users to project boulders.
 */
@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    /**
     * Finds all projects for the given user.
     *
     * @param userId identifier of the user
     * @return list of project entries
     */
    List<ProjectEntity> findByUserId(Long userId);

    /**
     * Finds a specific project entry for a user and boulder.
     *
     * @param userId identifier of the user
     * @param boulderId identifier of the boulder
     * @return optional containing the project if present
     */
    Optional<ProjectEntity> findByUserIdAndBoulderId(Long userId, Long boulderId);

    /**
     * Finds all projects for a user filtered by a set of boulder IDs.
     *
     * @param userId identifier of the user
     * @param boulderIds list of boulder identifiers
     * @return list of matching project entries
     */
    List<ProjectEntity> findByUserIdAndBoulderIdIn(Long userId, List<Long> boulderIds);

    /**
     * Finds all projects for a user in a specific gym.
     *
     * @param userId identifier of the user
     * @param gymId identifier of the gym
     * @return list of projects for boulders in the specified gym
     */
    List<ProjectEntity> findByUserIdAndBoulder_Sector_Gym_Id(Long userId, Long gymId);
}
