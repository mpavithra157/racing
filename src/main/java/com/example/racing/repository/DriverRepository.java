package com.example.racing.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.racing.model.Driver;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d FROM Driver d LEFT JOIN FETCH d.team LEFT JOIN FETCH d.registeredRaces")
    List<Driver> findAllWithRelations();
}