package de.othr.crusher.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Entity representing a user's marked project boulders.
 * <p>
 * Links a {@link UserEntity} to a {@link BoulderEntity} and enforces that each boulder
 * can only be marked once per user. The association is cascaded on delete so boulder
 * removal by setters does not fail because of project references.
 * </p>
 */
@Entity
@Table(
    name = "projects",
    uniqueConstraints = @UniqueConstraint(name = "uk_user_boulder_project", columnNames = {"user_id", "boulder_id"})
)
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(message = "Project must belong to a user")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boulder_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(message = "Project must belong to a boulder")
    private BoulderEntity boulder;

    @Column(name = "created_at", nullable = false)
    @NotNull(message = "Project must have a creation timestamp")
    private LocalDateTime createdAt;

    /**
     * Sets a creation timestamp before persisting.
     */
    @PrePersist
    public void populateCreatedAt() {
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

    public BoulderEntity getBoulder() {
        return boulder;
    }

    public void setBoulder(BoulderEntity boulder) {
        this.boulder = boulder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
