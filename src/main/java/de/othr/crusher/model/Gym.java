package de.othr.crusher.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gyms")
public class Gym {

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

    @OneToMany(
            mappedBy = "gym",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Sector> sectors = new ArrayList<>();

    public Gym() {}

    public Gym(String name, String street, String city, String email) {
        this.name = name;
        this.street = street;
        this.city = city;
        this.email = email;
    }

    // ---- Getters / Setters ----
    public Long getId() {
        return id;
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

    public List<Sector> getSectors() {
        return sectors;
    }

    public void addSector(Sector sector) {
        sectors.add(sector);
        sector.setGym(this);
    }

    public void removeSector(Sector sector) {
        sectors.remove(sector);
        sector.setGym(null);
    }
}
