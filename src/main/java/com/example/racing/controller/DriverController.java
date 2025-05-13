package com.example.racing.controller;

import com.example.racing.model.Driver;
import com.example.racing.model.Team;
import com.example.racing.service.DriverService;
import com.example.racing.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private TeamService teamService;

    @GetMapping
    public String listDrivers(Model model) {
        model.addAttribute("drivers", driverService.getAllDrivers());
        return "drivers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("driver", new Driver());
        model.addAttribute("teams", teamService.getAllTeams());
        return "drivers/form";
    }

    @PostMapping
    public String saveDriver(@ModelAttribute Driver driver) {
        driverService.saveDriver(driver);
        return "redirect:/drivers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Driver driver = driverService.getDriverById(id);
        List<Team> teams = teamService.getAllTeams();
        model.addAttribute("driver", driver);
        model.addAttribute("teams", teams);
        return "drivers/form";
    }

    @PostMapping("/update/{id}")
    public String updateDriver(@PathVariable Long id, @ModelAttribute Driver driver) {
        driverService.updateDriver(id, driver);
        return "redirect:/drivers";
    }

    @GetMapping("/delete/{id}")
    public String deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return "redirect:/drivers";
    }
}