package de.othr.crusher.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entity representing a notice for a gym.
 *
 * <p>Maps to the {@code notices} table and stores notice information including title, message,
 * creation date, and the associated gym.
 */
@Entity
@Table(name = "notices")
public class NoticeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @NotBlank(message = "Please enter a title")
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  @NotBlank(message = "Please enter a message")
  private String message;

  @Column(name = "creation_date", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime creationDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gym_id", nullable = false)
  private GymEntity gym;

  @Column(nullable = false)
  private boolean deleted = false;

  public NoticeEntity() {}

  public NoticeEntity(String title, String message, GymEntity gym) {
    this.title = title;
    this.message = message;
    this.gym = gym;
  }

  // ---- Getters / Setters ----
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public GymEntity getGym() {
    return gym;
  }

  public void setGym(GymEntity gym) {
    this.gym = gym;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
