package de.othr.crusher.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Entity representing an event for a gym.
 *
 * <p>Maps to the {@code events} table and stores event information including title, description,
 * schedule, and the associated gym.
 */
@Entity
@Table(name = "events")
public class EventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @NotBlank(message = "Please enter a title")
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  @NotBlank(message = "Please enter a description")
  private String description;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private boolean periodic;

  @Enumerated(EnumType.STRING)
  @Column(name = "weekday")
  private DayOfWeek weekday;

  @Column(name = "event_date")
  @DateTimeFormat(pattern = "dd.MM.yy")
  private LocalDate date;

  @Enumerated(EnumType.STRING)
  @Column(name = "recurrence_frequency")
  private EventFrequency frequency;

  @Column(nullable = false)
  @NotBlank(message = "Please enter a time")
  private String time;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gym_id", nullable = false)
  private GymEntity gym;

  @Column(nullable = false)
  private boolean deleted = false;

  public EventEntity() {}

  public EventEntity(String title, String description, GymEntity gym) {
    this.title = title;
    this.description = description;
    this.gym = gym;
  }

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public boolean isPeriodic() {
    return periodic;
  }

  public void setPeriodic(boolean periodic) {
    this.periodic = periodic;
  }

  public DayOfWeek getWeekday() {
    return weekday;
  }

  public void setWeekday(DayOfWeek weekday) {
    this.weekday = weekday;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public EventFrequency getFrequency() {
    return frequency;
  }

  public void setFrequency(EventFrequency frequency) {
    this.frequency = frequency;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
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
