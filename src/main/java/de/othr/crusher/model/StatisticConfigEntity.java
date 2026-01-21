package de.othr.crusher.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a user's statistics API configuration.
 *
 * <p>Stores which statistics the user wants to receive via the API and tracks the timestamp of the
 * last fetch to provide delta statistics.
 */
@Entity
@Table(name = "statistic_configs")
public class StatisticConfigEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private UserEntity user;

  @Column(name = "last_fetched_at")
  private LocalDateTime lastFetchedAt;

  @Column(name = "goes_per_grade_enabled", nullable = false)
  private boolean goesPerGradeEnabled;

  @Column(name = "finished_goes_per_grade_enabled", nullable = false)
  private boolean finishedGoesPerGradeEnabled;

  @Column(name = "result_distribution_enabled", nullable = false)
  private boolean resultDistributionEnabled;

  @Column(name = "highest_finished_grade_enabled", nullable = false)
  private boolean highestFinishedGradeEnabled;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
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

  public LocalDateTime getLastFetchedAt() {
    return lastFetchedAt;
  }

  public void setLastFetchedAt(LocalDateTime lastFetchedAt) {
    this.lastFetchedAt = lastFetchedAt;
  }

  public boolean isGoesPerGradeEnabled() {
    return goesPerGradeEnabled;
  }

  public void setGoesPerGradeEnabled(boolean goesPerGradeEnabled) {
    this.goesPerGradeEnabled = goesPerGradeEnabled;
  }

  public boolean isFinishedGoesPerGradeEnabled() {
    return finishedGoesPerGradeEnabled;
  }

  public void setFinishedGoesPerGradeEnabled(boolean finishedGoesPerGradeEnabled) {
    this.finishedGoesPerGradeEnabled = finishedGoesPerGradeEnabled;
  }

  public boolean isResultDistributionEnabled() {
    return resultDistributionEnabled;
  }

  public void setResultDistributionEnabled(boolean resultDistributionEnabled) {
    this.resultDistributionEnabled = resultDistributionEnabled;
  }

  public boolean isHighestFinishedGradeEnabled() {
    return highestFinishedGradeEnabled;
  }

  public void setHighestFinishedGradeEnabled(boolean highestFinishedGradeEnabled) {
    this.highestFinishedGradeEnabled = highestFinishedGradeEnabled;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
