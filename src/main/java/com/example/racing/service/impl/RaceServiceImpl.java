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

    private final RaceRepository raceRepository;

    @Autowired
    public RaceServiceImpl(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Override
    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    @Override
    public Race getRaceById(Long id) {
        Optional<Race> raceOptional = raceRepository.findById(id);
        return raceOptional.orElseThrow(() -> new IllegalArgumentException("Race not found with id: " + id));
    }

    @Override
    public void saveRace(Race race) {
        raceRepository.save(race);
    }

    @Override
    public void updateRace(Long id, Race updatedRace) {
        if (!raceRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot update. Race not found with id: " + id);
        }
        updatedRace.setId(id); // Ensure ID is preserved
        raceRepository.save(updatedRace);
    }

    @Override
    public void deleteRace(Long id) {
        if (!raceRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Race not found with id: " + id);
        }
        raceRepository.deleteById(id);
    }
}
