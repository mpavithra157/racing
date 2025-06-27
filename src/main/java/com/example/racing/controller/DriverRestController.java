package com.example.racing.controller;

import com.example.racing.model.Driver;
import com.example.racing.service.DriverService;
import com.example.racing.service.RaceService;
import com.example.racing.service.TeamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/drivers")
public class DriverRestController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private RaceService raceService;

    // ✅ GET all drivers
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    // ✅ GET one driver
    @GetMapping("/{id}")
    public Driver getDriver(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    // ✅ POST create driver
    @PostMapping
    public void createDriver(@RequestBody Driver driver) {
        driverService.saveDriver(driver); // no return
    }

    // ✅ PUT update driver
    @PutMapping("/{id}")
    public void updateDriver(@PathVariable Long id, @RequestBody Driver updatedDriver) {
        Driver existing = driverService.getDriverById(id);

        existing.setFirstName(updatedDriver.getFirstName());
        existing.setLastName(updatedDriver.getLastName());
        existing.setDob(updatedDriver.getDob());
        existing.setTeam(updatedDriver.getTeam());
        existing.setRegisteredRaces(updatedDriver.getRegisteredRaces());

        driverService.saveDriver(existing); // no return
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public void deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
    }
}