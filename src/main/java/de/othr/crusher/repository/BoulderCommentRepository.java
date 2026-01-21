package de.othr.crusher.repository;

import de.othr.crusher.model.BoulderCommentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link BoulderCommentEntity} entries that link users to boulder comments.
 */
@Repository
public interface BoulderCommentRepository extends JpaRepository<BoulderCommentEntity, Long> {

  /**
   * Finds all comments for a given boulder, ordered by creation date (newest first).
   *
   * @param boulderId identifier of the boulder
   * @return list of comments for the boulder, ordered by creation date
   */
  List<BoulderCommentEntity> findByBoulderIdOrderByCreatedAtDesc(Long boulderId);
}
