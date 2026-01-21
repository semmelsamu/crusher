package de.othr.crusher.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity representing a climbing attempt (go) on a boulder during a session.
 *
 * <p>Maps to the {@code goes} table and stores the attempt result and timestamp along with
 * references to the session and boulder.
 */
@Entity
@Table(name = "goes")
public class GoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", nullable = false)
  @NotNull(message = "Go must belong to a session")
  private SessionEntity session;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "boulder_id", nullable = false)
  @NotNull(message = "Go must belong to a boulder")
  private BoulderEntity boulder;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull(message = "Please select a result")
  private GoResult result;

  @Column(nullable = false)
  @NotNull(message = "Go must have a timestamp")
  private LocalDateTime timestamp;

  @Column(name = "progressed_hold")
  @Min(value = 0, message = "Progressed hold cannot be negative")
  private Integer progressedHold;

  public GoEntity() {}

  public GoEntity(
      SessionEntity session, BoulderEntity boulder, GoResult result, LocalDateTime timestamp) {
    this.session = session;
    this.boulder = boulder;
    this.result = result;
    this.timestamp = timestamp;
  }

  // ---- Getters / Setters ----
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public SessionEntity getSession() {
    return session;
  }

  public void setSession(SessionEntity session) {
    this.session = session;
  }

  public BoulderEntity getBoulder() {
    return boulder;
  }

  public void setBoulder(BoulderEntity boulder) {
    this.boulder = boulder;
  }

  public GoResult getResult() {
    return result;
  }

  public void setResult(GoResult result) {
    this.result = result;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Integer getProgressedHold() {
    return progressedHold;
  }

  public void setProgressedHold(Integer progressedHold) {
    this.progressedHold = progressedHold;
  }
}
