package com.example.racing.service.impl;

import com.example.racing.model.Driver;
import com.example.racing.repository.DriverRepository;
import com.example.racing.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public Driver getDriverById(Long id) {
        return driverRepository.findById(id).orElse(null);
    }

    @Override
    public void saveDriver(Driver driver) {
        driverRepository.save(driver);
    }

    @Override
    public void updateDriver(Long id, Driver updatedDriver) {
        Optional<Driver> existingOpt = driverRepository.findById(id);
        if (existingOpt.isPresent()) {
            Driver existing = existingOpt.get();
            existing.setFirstName(updatedDriver.getFirstName());
            existing.setLastName(updatedDriver.getLastName());
            existing.setDob(updatedDriver.getDob());
            existing.setTeam(updatedDriver.getTeam());
            existing.setRegisteredRaces(updatedDriver.getRegisteredRaces());
            driverRepository.save(existing);
        }
    }

    @Override
    public void deleteDriver(Long id) {
        Driver driver = getDriverById(id);
        if (driver != null && (driver.getRegisteredRaces() == null || driver.getRegisteredRaces().isEmpty())) {
            driverRepository.deleteById(id);
        } else {
            throw new IllegalStateException("Driver cannot be deleted. They have registered races.");
        }
    }
}
