package com.example.racing.service;

import com.example.racing.model.Team;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TeamService {
    List<Team> getAllTeams();

    Team getTeamById(Long id);

    void saveTeamWithLogo(Team team, MultipartFile file) throws IOException;

    void updateTeam(Long id, Team updatedTeam, MultipartFile file) throws IOException;

    void deleteTeam(Long id);

    byte[] getTeamLogoById(Long id);
}
