package com.example.racing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 96, nullable = false)
    @NotBlank
    private String firstName;

    @Column(length = 96, nullable = false)
    @NotBlank
    private String lastName;

    @Column(nullable = false)
    @Past
    @NotNull(message = "Date of Birth is required")
    private LocalDate dob;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToMany
    @JoinTable(name = "driver_race", joinColumns = @JoinColumn(name = "driver_id"), inverseJoinColumns = @JoinColumn(name = "race_id"))
    private Set<Race> registeredRaces = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Set<Race> getRegisteredRaces() {
        return registeredRaces;
    }

    public void setRegisteredRaces(Set<Race> registeredRaces) {
        this.registeredRaces = registeredRaces;
    }
}
