package de.othr.crusher.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a climbing gym.
 * <p>
 * Maps to the {@code gyms} table and stores the gym's contact details (name, street,
 * city, email) along with its associated sectors and grades.
 * </p>
 */
@Entity
@Table(name = "gyms")
public class GymEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Please enter a name")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Please enter a street")
    private String street;

    @Column(nullable = false)
    @NotBlank(message = "Please enter a city")
    private String city;

    @Column(nullable = false)
    @NotBlank(message = "Please enter a email")
    @Email(message = "Please enter a correct email")
    private String email;

    @Column(nullable = true)
    private String crowdLevelUrl;

    @OneToMany(
            mappedBy = "gym",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<SectorEntity> sectors = new ArrayList<>();

    @OneToMany(
            mappedBy = "gym",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<GradeEntity> grades = new ArrayList<>();

    @OneToMany(
            mappedBy = "gym",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<NoticeEntity> notices = new ArrayList<>();

    public GymEntity() {}

    public GymEntity(String name, String street, String city, String email) {
        this.name = name;
        this.street = street;
        this.city = city;
        this.email = email;
    }

    // ---- Getters / Setters ----
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCrowdLevelUrl() {
        return crowdLevelUrl;
    }

    public void setCrowdLevelUrl(String crowdLevelUrl) {
        this.crowdLevelUrl = crowdLevelUrl;
    }

    public List<SectorEntity> getSectors() {
        return sectors;
    }

    public List<GradeEntity> getGrades() {
        return grades;
    }

    public List<NoticeEntity> getNotices() {
        return notices;
    }

    public void addSector(SectorEntity sector) {
        sectors.add(sector);
        sector.setGym(this);
    }

    public void removeSector(SectorEntity sector) {
        sectors.remove(sector);
        sector.setGym(null);
    }

    public void addGrade(GradeEntity grade) {
        grades.add(grade);
        grade.setGym(this);
    }

    public void removeGrade(GradeEntity grade) {
        grades.remove(grade);
        grade.setGym(null);
    }

    public void addNotice(NoticeEntity notice) {
        notices.add(notice);
        notice.setGym(this);
    }

    public void removeNotice(NoticeEntity notice) {
        notices.remove(notice);
        notice.setGym(null);
    }
}
