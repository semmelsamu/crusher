package de.othr.crusher.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Entity representing a climbing sector belonging to exactly one gym.
 * <p>
 * Maps to the {@code sectors} table and stores the sector name, optional description,
 * an image path (defaults to a placeholder), and a reference to its owning gym.
 * </p>
 */
@Entity
@Table(name = "sectors")
public class SectorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Please enter a name")
    private String name;

    @Column
    private String description;

    @Column(name = "image_path")
    private String imagePath = "/images/default-sector.svg";

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "gym_id", nullable = false)
    private GymEntity gym;

    public SectorEntity() {}

    public SectorEntity(String name, String description, String imagePath, GymEntity gym) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.gym = gym;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public GymEntity getGym() {
        return gym;
    }

    public void setGym(GymEntity gym) {
        this.gym = gym;
    }
}
