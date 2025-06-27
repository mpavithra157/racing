package com.example.racing.controller;

import com.example.racing.model.Race;
import com.example.racing.model.Driver;
import com.example.racing.service.RaceService;
import com.example.racing.service.TeamService;
import com.example.racing.service.DriverService;
import jakarta.validation.Valid;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/races")
public class RaceController {

    @Autowired
    private RaceService raceService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private DriverService driverService;

    @GetMapping
    public String listRaces(Model model) {
        model.addAttribute("races", raceService.getAllRaces());
        return "races/list";
    }

    @GetMapping("/new")
    public String showCreateRaceForm(Model model) {
        model.addAttribute("race", new Race());
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("drivers", driverService.getAllDrivers());
        return "races/form";
    }

    @PostMapping("/save")
    public String saveRace(@Valid @ModelAttribute Race race, BindingResult result,
            @RequestParam(value = "drivers", required = false) List<Long> driverIds, Model model) {
        if (result.hasErrors()) {

            result.getAllErrors().forEach(error -> {
                System.out.println("Validation Error: " + error.getDefaultMessage());
            });
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("drivers", driverService.getAllDrivers());
            return "races/form";
        }

        // Fetch and set the actual Driver entities by ID
        if (driverIds != null && !driverIds.isEmpty()) {
            Set<Driver> drivers = driverIds.stream()
                    .map(driverService::getDriverById)
                    .filter(d -> d != null)
                    .collect(Collectors.toSet());
            race.setDrivers(drivers);
        } else {
            race.setDrivers(new HashSet<>()); // In case none selected
        }
        raceService.saveRace(race);
        return "redirect:/races";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Race race = raceService.getRaceById(id);
        model.addAttribute("race", race);
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("drivers", driverService.getAllDrivers());
        return "races/form";
    }

    @PostMapping("/update/{id}")
    public String updateRace(@PathVariable Long id, @Valid @ModelAttribute Race race, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("drivers", driverService.getAllDrivers());
            return "races/form";
        }
        raceService.updateRace(id, race);
        return "redirect:/races";
    }

    @GetMapping("/delete/{id}")
    public String deleteRace(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Race race = raceService.getRaceById(id);

        if (race.getDrivers() != null && !race.getDrivers().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete race with registered drivers.");
            return "redirect:/races";
        }

        raceService.deleteRace(id);
        return "redirect:/races";
    }
}
