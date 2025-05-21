package com.example.racing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 96, nullable = false)
    @NotBlank
    private String raceTrackName;

    @Column(nullable = false)
    @NotBlank
    private String location;

    @Column(nullable = false)
    @Future
    @NotNull(message = "Date is required")
    private LocalDate date;

    @Column
    private LocalDate registrationClosureDate; // Optional field

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToMany(mappedBy = "registeredRaces")
    private Set<Driver> drivers = new HashSet<>();

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRaceTrackName() {
        return raceTrackName;
    }

    public void setRaceTrackName(String raceTrackName) {
        this.raceTrackName = raceTrackName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getRegistrationClosureDate() {
        return registrationClosureDate;
    }

    public void setRegistrationClosureDate(LocalDate registrationClosureDate) {
        this.registrationClosureDate = registrationClosureDate;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Set<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(Set<Driver> drivers) {
        this.drivers = drivers;
    }
}
