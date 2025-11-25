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
import jakarta.validation.constraints.NotBlank;

/**
 * Entity representing a bouldering grade within a gym.
 * <p>
 * Stores human-readable identifiers (name), along with scale values (V-scale and
 * Font-scale) and an optional description. Each grade belongs to exactly one gym.
 * </p>
 */
@Entity
@Table(name = "grades")
public class GradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Please enter a name")
    private String name;

    @Column(name = "v_scale", nullable = false)
    @NotBlank(message = "Please enter a V-scale value")
    private String vScale;

    @Column(name = "font_scale", nullable = false)
    @NotBlank(message = "Please enter a Font-scale value")
    private String fontScale;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private GymEntity gym;

    public GradeEntity() {}

    public GradeEntity(String name, String vScale, String fontScale, String description, GymEntity gym) {
        this.name = name;
        this.vScale = vScale;
        this.fontScale = fontScale;
        this.description = description;
        this.gym = gym;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVScale() {
        return vScale;
    }

    public void setVScale(String vScale) {
        this.vScale = vScale;
    }

    public String getFontScale() {
        return fontScale;
    }

    public void setFontScale(String fontScale) {
        this.fontScale = fontScale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GymEntity getGym() {
        return gym;
    }

    public void setGym(GymEntity gym) {
        this.gym = gym;
    }
}
