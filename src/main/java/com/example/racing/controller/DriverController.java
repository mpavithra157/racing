package com.example.racing.controller;

import com.example.racing.model.Driver;
import com.example.racing.model.Team;
import com.example.racing.model.Race;
import com.example.racing.service.DriverService;
import com.example.racing.service.TeamService;
import com.example.racing.service.RaceService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private RaceService raceService;

    @GetMapping
    public String listDrivers(Model model) {
        model.addAttribute("drivers", driverService.getAllDrivers());
        return "drivers/list";
    }

    @GetMapping("/new")
    public String showCreateDriverForm(Model model) {
        model.addAttribute("driver", new Driver());
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("races", raceService.getAllRaces());
        return "drivers/form";
    }

    @PostMapping("/save")
    public String saveDriver(@Valid @ModelAttribute Driver driver, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("teams", teamService.getAllTeams());
            return "drivers/form";
        }
        driverService.saveDriver(driver);
        return "redirect:/drivers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Driver driver = driverService.getDriverById(id);
        model.addAttribute("driver", driver);
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("races", raceService.getAllRaces());
        return "drivers/form";
    }

    @PostMapping("/update/{id}")
    public String updateDriver(@PathVariable Long id, @Valid @ModelAttribute Driver driver, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("teams", teamService.getAllTeams());
            return "drivers/form";
        }
        driverService.updateDriver(id, driver);
        return "redirect:/drivers";
    }

    @GetMapping("/delete/{id}")
    public String deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return "redirect:/drivers";
    }
}
