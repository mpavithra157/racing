package com.example.racing.service;

import com.example.racing.model.Race;
import java.util.List;

public interface RaceService {
    List<Race> getAllRaces();

    Race getRaceById(Long id);

    void saveRace(Race race);

    void updateRace(Long id, Race updatedRace);

    void deleteRace(Long id);
}