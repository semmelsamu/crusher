package de.othr.crusher.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


import java.io.Serializable;

@Entity
@Table(name = "gyms")
public class Gym {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String email;

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
}
