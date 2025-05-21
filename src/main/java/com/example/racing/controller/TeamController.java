package com.example.racing.controller;

import com.example.racing.model.Team;
import com.example.racing.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    public String listTeams(Model model) {
        model.addAttribute("teams", teamService.getAllTeams());
        return "teams/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("team", new Team());
        return "teams/form";
    }

    @PostMapping("/save")
    public String saveTeam(@Valid @ModelAttribute("team") Team team,
            @RequestParam(value = "logoFile", required = false) MultipartFile logofile,
            BindingResult result, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("team", team);
            return "teams/form";
        }
        teamService.saveTeamWithLogo(team, logofile);
        return "redirect:/teams";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("team", teamService.getTeamById(id));
        return "teams/form";
    }

    @PostMapping("/update/{id}")
    public String updateTeam(@PathVariable Long id,
            @Valid @ModelAttribute("team") Team team,
            @RequestParam("logoFile") MultipartFile file,
            BindingResult result) throws IOException {
        if (result.hasErrors()) {
            return "teams/form";
        }
        teamService.updateTeam(id, team, file);
        return "redirect:/teams";
    }

    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id, Model model) {
        try {
            teamService.deleteTeam(id);
        } catch (IllegalStateException e) {
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("error", e.getMessage());
            return "teams/list";
        }
        return "redirect:/teams";
    }

    @GetMapping("/logo/{id}")
    public ResponseEntity<byte[]> getTeamLogo(@PathVariable Long id) {
        byte[] logo = teamService.getTeamLogoById(id); // You must implement this in your service
        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg") // or determine type dynamically if needed
                .body(logo);
    }
}