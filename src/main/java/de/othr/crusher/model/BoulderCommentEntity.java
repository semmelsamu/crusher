package de.othr.crusher.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entity representing a user's comment on a boulder.
 *
 * <p>Each comment has a user, boulder, comment text, and creation timestamp.
 */
@Entity
@Table(name = "boulder_comments")
public class BoulderCommentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @NotNull(message = "User is required")
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "boulder_id", nullable = false)
  @NotNull(message = "Boulder is required")
  private BoulderEntity boulder;

  @Column(nullable = false, columnDefinition = "TEXT")
  @NotBlank(message = "Comment text is required")
  private String comment;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column private LocalDateTime updatedAt;

  public BoulderCommentEntity() {}

  public BoulderCommentEntity(UserEntity user, BoulderEntity boulder, String comment) {
    this.user = user;
    this.boulder = boulder;
    this.comment = comment;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public BoulderEntity getBoulder() {
    return boulder;
  }

  public void setBoulder(BoulderEntity boulder) {
    this.boulder = boulder;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
