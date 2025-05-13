package com.example.racing.service.impl;

import com.example.racing.model.Team;
import com.example.racing.repository.TeamRepository;
import com.example.racing.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @Override
    public Team getTeamById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    @Override
    public void saveTeamWithLogo(Team team, MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            team.setLogo(file.getBytes());
        }
        teamRepository.save(team);
    }

    @Override
    public void updateTeam(Long id, Team updatedTeam, MultipartFile file) throws IOException {
        Optional<Team> existingTeamOpt = teamRepository.findById(id);
        if (existingTeamOpt.isPresent()) {
            Team existingTeam = existingTeamOpt.get();
            existingTeam.setName(updatedTeam.getName());
            existingTeam.setLocation(updatedTeam.getLocation());
            existingTeam.setDescription(updatedTeam.getDescription());
            if (!file.isEmpty()) {
                existingTeam.setLogo(file.getBytes());
            }
            teamRepository.save(existingTeam);
        }
    }

    @Override
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }
}
