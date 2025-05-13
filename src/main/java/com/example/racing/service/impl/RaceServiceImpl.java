package com.example.racing.service.impl;

import com.example.racing.model.Race;
import com.example.racing.repository.RaceRepository;
import com.example.racing.service.RaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RaceServiceImpl implements RaceService {

    @Autowired
    private RaceRepository raceRepository;

    @Override
    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    @Override
    public Race getRaceById(Long id) {
        return raceRepository.findById(id).orElse(null);
    }

    @Override
    public void saveRace(Race race) {
        raceRepository.save(race);
    }

    @Override
    public void updateRace(Long id, Race updatedRace) {
        Optional<Race> existingRaceOpt = raceRepository.findById(id);
        if (existingRaceOpt.isPresent()) {
            Race existingRace = existingRaceOpt.get();
            existingRace.setRaceName(updatedRace.getRaceName());
            existingRace.setLocation(updatedRace.getLocation());
            existingRace.setTeam(updatedRace.getTeam());
            raceRepository.save(existingRace);
        }
    }

    @Override
    public void deleteRace(Long id) {
        raceRepository.deleteById(id);
    }
}