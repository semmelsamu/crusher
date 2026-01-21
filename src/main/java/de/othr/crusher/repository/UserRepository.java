package de.othr.crusher.repository;

import de.othr.crusher.model.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing and managing {@link UserEntity} data.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations and adds a custom method to
 * find users by their username.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  /**
   * Finds a user by their username.
   *
   * @param name the username to search for
   * @return an {@link Optional} containing the matching {@link UserEntity}, or empty if no user was
   *     found
   */
  Optional<UserEntity> findByName(String name);

  /**
   * Finds a user by their username if they are not deleted.
   *
   * @param name the username to search for
   * @return an {@link Optional} containing the matching {@link UserEntity}, or empty if no user was
   *     found
   */
  Optional<UserEntity> findByNameAndDeletedFalse(String name);

  /**
   * Finds a user by their email address.
   *
   * @param email the email address to search for
   * @return an {@link Optional} containing the matching {@link UserEntity}, or empty if no user was
   *     found
   */
  Optional<UserEntity> findByEmail(String email);

  /**
   * Finds all users that are not deleted.
   *
   * @return a list of all non-deleted users
   */
  java.util.List<UserEntity> findByDeletedFalse();
}
