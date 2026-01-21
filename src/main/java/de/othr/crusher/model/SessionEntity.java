package de.othr.crusher.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity representing a climbing session at a specific gym.
 *
 * <p>Maps to the {@code sessions} table and stores the session timing (started and ended
 * timestamps) along with references to the user who created the session and the gym where it took
 * place.
 */
@Entity
@Table(name = "sessions")
public class SessionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "started_at", nullable = false)
  @NotNull(message = "Session must have a start time")
  private LocalDateTime startedAt;

  @Column(name = "ended_at")
  private LocalDateTime endedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @NotNull(message = "Session must belong to a user")
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gym_id", nullable = false)
  @NotNull(message = "Session must belong to a gym")
  private GymEntity gym;

  public SessionEntity() {}

  public SessionEntity(
      LocalDateTime startedAt, LocalDateTime endedAt, UserEntity user, GymEntity gym) {
    this.startedAt = startedAt;
    this.endedAt = endedAt;
    this.user = user;
    this.gym = gym;
  }

  // ---- Getters / Setters ----
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(LocalDateTime startedAt) {
    this.startedAt = startedAt;
  }

  public LocalDateTime getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(LocalDateTime endedAt) {
    this.endedAt = endedAt;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public GymEntity getGym() {
    return gym;
  }

  public void setGym(GymEntity gym) {
    this.gym = gym;
  }

  /**
   * Checks if the session is currently running (no end time set).
   *
   * @return true if the session is running, false otherwise
   */
  public boolean isRunning() {
    return endedAt == null;
  }
}
