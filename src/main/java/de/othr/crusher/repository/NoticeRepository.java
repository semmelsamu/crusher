package de.othr.crusher.repository;

import de.othr.crusher.model.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {
    /**
     * Finds all notices for a specific gym.
     *
     * @param gymId the ID of the gym
     * @return list of notices for the gym
     */
    List<NoticeEntity> findByGymId(Long gymId);
}
