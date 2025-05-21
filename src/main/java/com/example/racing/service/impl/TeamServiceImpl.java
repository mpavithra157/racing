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
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
    }

    @Override
    public void saveTeamWithLogo(Team team, MultipartFile logofile) throws IOException {
        System.out.println("Is file empty: " + (logofile == null || logofile.isEmpty()));
        System.out.println("Incoming Team ID: " + team.getId());

        boolean isNew = team.getId() == null;

        if (isNew) {
            if (logofile == null || logofile.isEmpty()) {
                throw new IllegalArgumentException("Logo is mandatory");
            }

            if (logofile != null && !logofile.isEmpty()) {
                if (logofile.getSize() > 51200) { // 50 KB = 50 * 1024 bytes = 51200
                    throw new IllegalArgumentException("Logo file size exceeds 50KB");
                }
                team.setLogo(logofile.getBytes());
            }

            teamRepository.save(team);
        }
    }

    @Override
    public void updateTeam(Long id, Team updatedTeam, MultipartFile file) throws IOException {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isPresent()) {
            Team existingTeam = optionalTeam.get();

            existingTeam.setName(updatedTeam.getName());
            existingTeam.setLocation(updatedTeam.getLocation());
            existingTeam.setDescription(updatedTeam.getDescription());

            if (!file.isEmpty()) {
                if (file.getSize() > 51200) {
                    throw new IllegalArgumentException("Logo file size exceeds 50KB");
                }
                existingTeam.setLogo(file.getBytes());
            }

            teamRepository.save(existingTeam);
        } else {
            throw new RuntimeException("Team not found with id: " + id);
        }
    }

    @Override
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (team.getRaces() != null && !team.getRaces().isEmpty()) {
            throw new IllegalStateException("Cannot delete team registered in at least one race.");
        }

        teamRepository.delete(team);
    }

    @Override
    public byte[] getTeamLogoById(Long id) {
        Team team = teamRepository.findById(id).orElseThrow(() -> new RuntimeException("Team not found"));
        return team.getLogo();
    }
}
