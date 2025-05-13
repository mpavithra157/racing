package com.example.racing.controller;

import com.example.racing.model.Race;
import com.example.racing.service.RaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/races")
public class RaceController {

    @Autowired
    private RaceService raceService;

    @GetMapping
    public String listRaces(Model model) {
        model.addAttribute("races", raceService.getAllRaces());
        return "races/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("race", new Race());
        return "races/form";
    }

    @PostMapping("/save")
    public String createRace(@Valid @ModelAttribute("race") Race race,
            BindingResult result) {
        if (result.hasErrors()) {
            return "races/form";
        }
        raceService.saveRace(race);
        return "redirect:/races";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("race", raceService.getRaceById(id));
        return "races/form";
    }

    @PostMapping("/update/{id}")
    public String updateRace(@PathVariable Long id,
            @Valid @ModelAttribute("race") Race race,
            BindingResult result) {
        if (result.hasErrors()) {
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