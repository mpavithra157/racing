package com.example.racing.controller;

import com.example.racing.model.Team;
import com.example.racing.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String createTeam(@Valid @ModelAttribute("team") Team team,
            @RequestParam("logoFile") MultipartFile file,
            BindingResult result) throws IOException {
        if (result.hasErrors()) {
            return "teams/form";
        }
        teamService.saveTeamWithLogo(team, file);
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
    public String deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return "redirect:/teams";
    }
}