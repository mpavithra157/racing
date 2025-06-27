package com.example.racing.controller;

import com.example.racing.model.Team;
import com.example.racing.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import java.util.stream.Collectors;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000") // Allow calls from React frontend
@RestController // ✅ Tells Spring this is a REST API, not a Thymeleaf controller
@RequestMapping("/api/teams") // ✅ All requests to /api/teams will come here
public class TeamRestController {

    @Autowired
    private TeamService teamService;

    // GET /api/teams
    @GetMapping
    public List<Team> getAllTeams() {
        return teamService.getAllTeams(); // ✅ This returns JSON, not a view
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        Team team = teamService.getTeamById(id);
        if (team != null) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Create new team (POST /api/teams)
    @PostMapping
    public ResponseEntity<String> createTeam(
            @RequestParam("name") String name,
            @RequestParam("location") String location,
            @RequestParam("description") String description,
            @RequestParam("logoFile") MultipartFile logoFile) {
        try {
            Team team = new Team();
            team.setName(name);
            team.setLocation(location);
            team.setDescription(description);
            team.setLogo(logoFile.getBytes());

            teamService.saveTeam(team);
            return ResponseEntity.ok("Team created successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to save team");
        }
    }

    // ✅ Update team (PUT /api/teams/{id})
    @PutMapping("/{id}")
    public ResponseEntity<String> updateTeam(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("location") String location,
            @RequestParam("description") String description,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile) {
        try {
            Team existingTeam = teamService.getTeamById(id);
            if (existingTeam == null) {
                return ResponseEntity.notFound().build();
            }

            existingTeam.setName(name);
            existingTeam.setLocation(location);
            existingTeam.setDescription(description);

            if (logoFile != null && !logoFile.isEmpty()) {
                existingTeam.setLogo(logoFile.getBytes());
            }

            teamService.saveTeam(existingTeam);
            return ResponseEntity.ok("Team updated successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to update team");
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/logo/{id}")
    public ResponseEntity<byte[]> getTeamLogo(@PathVariable Long id) {
        byte[] logo = teamService.getTeamLogoById(id);

        if (logo == null || logo.length == 0) {
            return ResponseEntity.notFound().build();
        }

        // Optional: detect content type
        String contentType = "image/jpeg";
        if (logo.length >= 4 &&
                (logo[0] & 0xFF) == 0x89 &&
                (logo[1] & 0xFF) == 0x50 &&
                (logo[2] & 0xFF) == 0x4E &&
                (logo[3] & 0xFF) == 0x47) {
            contentType = "image/png";
        }

        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .header("Access-Control-Allow-Origin", "http://localhost:3000")
                .header("Access-Control-Allow-Credentials", "true")
                .body(logo);
    }

}
