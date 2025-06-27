package com.example.racing.controller;

import com.example.racing.model.Driver;
import com.example.racing.model.Race;
import com.example.racing.model.Team;
import com.example.racing.service.DriverService;
import com.example.racing.service.RaceService;
import com.example.racing.service.TeamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/races")
public class RaceRestController {

    @Autowired
    private RaceService raceService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private DriverService driverService;

    // ✅ Get all races
    @GetMapping
    public List<Race> getAllRaces() {
        return raceService.getAllRaces();
    }

    // ✅ Get race by ID
    @GetMapping("/{id}")
    public Race getRaceById(@PathVariable Long id) {
        return raceService.getRaceById(id);
    }

    // ✅ Create new race
    @PostMapping
    public void createRace(@RequestBody Race race) {
        resolveTeamAndDrivers(race);
        raceService.saveRace(race);
    }

    // ✅ Update race
    @PutMapping("/{id}")
    public void updateRace(@PathVariable Long id, @RequestBody Race updatedRace) {
        Race existingRace = raceService.getRaceById(id);

        existingRace.setRaceTrackName(updatedRace.getRaceTrackName());
        existingRace.setLocation(updatedRace.getLocation());
        existingRace.setDate(updatedRace.getDate());
        existingRace.setRegistrationClosureDate(updatedRace.getRegistrationClosureDate());

        existingRace.setTeam(teamService.getTeamById(updatedRace.getTeam().getId()));

        Set<Driver> drivers = updatedRace.getDrivers()
                .stream()
                .map(d -> driverService.getDriverById(d.getId()))
                .collect(Collectors.toSet());

        existingRace.setDrivers(drivers);

        raceService.saveRace(existingRace);
    }

    // ✅ Delete race
    @DeleteMapping("/{id}")
    public void deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
    }

    // ✅ Helper to resolve team & drivers from ID before saving
    private void resolveTeamAndDrivers(Race race) {
        // Load full Team object
        Team team = teamService.getTeamById(race.getTeam().getId());
        race.setTeam(team);

        // Load full Driver objects
        Set<Driver> drivers = race.getDrivers()
                .stream()
                .map(driver -> driverService.getDriverById(driver.getId()))
                .collect(Collectors.toSet());

        race.setDrivers(drivers);
    }
}