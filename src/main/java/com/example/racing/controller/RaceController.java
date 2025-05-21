package com.example.racing.controller;

import com.example.racing.model.Race;
import com.example.racing.service.RaceService;
import com.example.racing.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/races")
public class RaceController {

    @Autowired
    private RaceService raceService;

    @Autowired
    private TeamService teamService;

    @GetMapping
    public String listRaces(Model model) {
        model.addAttribute("races", raceService.getAllRaces());
        return "races/list";
    }

    @GetMapping("/new")
    public String showCreateRaceForm(Model model) {
        model.addAttribute("race", new Race());
        model.addAttribute("teams", teamService.getAllTeams());
        return "races/form";
    }

    @PostMapping("/save")
    public String saveRace(@Valid @ModelAttribute Race race, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("teams", teamService.getAllTeams());
            return "races/form";
        }
        raceService.saveRace(race);
        return "redirect:/races";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Race race = raceService.getRaceById(id);
        model.addAttribute("race", race);
        model.addAttribute("teams", teamService.getAllTeams());
        return "races/form";
    }

    @PostMapping("/update/{id}")
    public String updateRace(@PathVariable Long id, @Valid @ModelAttribute Race race, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("teams", teamService.getAllTeams());
            return "races/form";
        }
        raceService.updateRace(id, race);
        return "redirect:/races";
    }

    @GetMapping("/delete/{id}")
    public String deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
        return "redirect:/races";
    }
}
