package com.example.racing.controller;

import com.example.racing.model.Team;
import com.example.racing.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
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

        try {
            teamService.saveTeamWithLogo(team, logofile);
        } catch (DataIntegrityViolationException e) {
            result.rejectValue("name", "error.team", "Team name must be unique.");
            return "teams/form";
        }

        return "redirect:/teams";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("team", teamService.getTeamById(id));
        return "teams/form";
    }

    @PostMapping("/update/{id}")
    public String updateTeam(@PathVariable Long id,
            @ModelAttribute("team") Team team,
            BindingResult result,
            @RequestParam("logoFile") MultipartFile file,
            Model model) throws IOException {

        // Manual validation
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            result.rejectValue("name", "error.team", "Team name is required.");
        } else if (team.getName().length() > 256) {
            result.rejectValue("name", "error.team", "Team name must not exceed 256 characters.");
        }

        if (result.hasErrors()) {
            model.addAttribute("team", team);
            return "teams/form";
        }

        try {
            teamService.updateTeam(id, team, file);
        } catch (DataIntegrityViolationException e) {
            result.rejectValue("name", "error.team", "Team name must be unique.");
            model.addAttribute("team", team);
            return "teams/form";
        }

        return "redirect:/teams";
    }

    @InitBinder
    public void disableAutoValidation(WebDataBinder binder) {
        binder.setValidator(null); // disables automatic validation
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
        byte[] logo = teamService.getTeamLogoById(id);
        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(logo);
    }
}