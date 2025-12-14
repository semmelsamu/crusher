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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing a boulder problem within a sector.
 * <p>
 * Each boulder has a description, a color tag, and belongs to a specific grade and sector.
 * </p>
 */
@Entity
@Table(name = "boulders")
public class BoulderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Please enter a description")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please select a color")
    private BoulderColor color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    @NotNull(message = "Please select a grade")
    private GradeEntity grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private SectorEntity sector;

    @Column(name = "holds_count", nullable = false)
    @NotNull(message = "Please enter the number of holds")
    @Min(value = 1, message = "Boulder must have at least one hold")
    private Integer holdsCount;

    public BoulderEntity() {}

    public BoulderEntity(String description, BoulderColor color, GradeEntity grade, SectorEntity sector) {
        this.description = description;
        this.color = color;
        this.grade = grade;
        this.sector = sector;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BoulderColor getColor() {
        return color;
    }

    public void setColor(BoulderColor color) {
        this.color = color;
    }

    public GradeEntity getGrade() {
        return grade;
    }

    public void setGrade(GradeEntity grade) {
        this.grade = grade;
    }

    public SectorEntity getSector() {
        return sector;
    }

    public void setSector(SectorEntity sector) {
        this.sector = sector;
    }

    public Integer getHoldsCount() {
        return holdsCount;
    }

    public void setHoldsCount(Integer holdsCount) {
        this.holdsCount = holdsCount;
    }
}
