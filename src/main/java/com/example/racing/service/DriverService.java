package com.example.racing.service;

import com.example.racing.model.Driver;
import java.util.List;

public interface DriverService {
    List<Driver> getAllDrivers();

    Driver getDriverById(Long id);

    void saveDriver(Driver driver);

    void updateDriver(Long id, Driver driver);

    void deleteDriver(Long id);
}
