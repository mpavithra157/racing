package com.example.racing.controller

import com.example.racing.model.Driver
import com.example.racing.model.Race
import com.example.racing.service.DriverService
import com.example.racing.service.RaceService
import com.example.racing.service.TeamService
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import spock.lang.Specification

class RaceControllerSpec extends Specification {

    def raceService = Mock(RaceService)
    def teamService = Mock(TeamService)
    def driverService = Mock(DriverService)
    def controller = new RaceController()

    def setup() {
        controller.raceService = raceService
        controller.teamService = teamService
        controller.driverService = driverService
    }

    def "listRaces should add races to model and return view"() {
        given:
        def model = Mock(Model)
        def races = [new Race()]
        raceService.getAllRaces() >> races

        when:
        def view = controller.listRaces(model)

        then:
        1 * model.addAttribute("races", races)
        view == "races/list"
    }

    def "showCreateRaceForm should add race, teams, and drivers to model"() {
        given:
        def model = Mock(Model)

        when:
        def view = controller.showCreateRaceForm(model)

        then:
        1 * model.addAttribute("race", _ as Race)
        1 * model.addAttribute("teams", _)
        1 * model.addAttribute("drivers", _)
        view == "races/form"
    }

    def "saveRace with validation errors should return form view"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> true
            getAllErrors() >> []
        }
        def race = new Race()

        when:
        def view = controller.saveRace(race, bindingResult, [1L, 2L], model)

        then:
        1 * model.addAttribute("teams", _)
        1 * model.addAttribute("drivers", _)
        view == "races/form"
    }

    def "saveRace with valid data should save and redirect"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> false
        }
        def race = new Race()
        def driver1 = new Driver(id: 1L)
        def driver2 = new Driver(id: 2L)
        driverService.getDriverById(1L) >> driver1
        driverService.getDriverById(2L) >> driver2

        when:
        def view = controller.saveRace(race, bindingResult, [1L, 2L], model)

        then:
        1 * raceService.saveRace({ it.drivers.containsAll([driver1, driver2]) })
        view == "redirect:/races"
    }

    def "showEditForm should populate model with race, teams, and drivers"() {
        given:
        def model = Mock(Model)
        def race = new Race(id: 1L)
        raceService.getRaceById(1L) >> race
        when:
        def view = controller.showEditForm(1L, model)

        then:
        1 * model.addAttribute("race", race)
        1 * model.addAttribute("teams", _)
        1 * model.addAttribute("drivers", _)
        view == "races/form"
    }

    def "updateRace with validation errors should return form view"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> true
        }
        def race = new Race()

        when:
        def view = controller.updateRace(1L, race, bindingResult, model)

        then:
        1 * model.addAttribute("teams", _)
        1 * model.addAttribute("drivers", _)
        view == "races/form"
    }

    def "updateRace with valid data should update and redirect"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> false
        }
        def race = new Race()

        when:
        def view = controller.updateRace(1L, race, bindingResult, model)

        then:
        1 * raceService.updateRace(1L, race)
        view == "redirect:/races"
    }

    def "deleteRace should delete and redirect if no drivers registered"() {
        given:
        def redirectAttributes = Mock(RedirectAttributes)
        def race = new Race(drivers: [] as Set)
        raceService.getRaceById(1L) >> race

        when:
        def view = controller.deleteRace(1L, redirectAttributes)

        then:
        1 * raceService.deleteRace(1L)
        view == "redirect:/races"
    }

    def "deleteRace should not delete and show error if drivers registered"() {
        given:
        def redirectAttributes = Mock(RedirectAttributes)
        def race = new Race(drivers: [new Driver()] as Set)
        raceService.getRaceById(2L) >> race

        when:
        def view = controller.deleteRace(2L, redirectAttributes)

        then:
        1 * redirectAttributes.addFlashAttribute("error", "Cannot delete race with registered drivers.")
        0 * raceService.deleteRace(_)
        view == "redirect:/races"
    }

    def "deleteRace should delete and redirect if drivers is null"() {
        given:
        def redirectAttributes = Mock(RedirectAttributes)
        def race = new Race(drivers: null)
        raceService.getRaceById(3L) >> race
    
        when:
        def view = controller.deleteRace(3L, redirectAttributes)
    
        then:
        1 * raceService.deleteRace(3L)
        view == "redirect:/races"
    }

    def "saveRace should handle null driver IDs gracefully"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> false
        }
        def race = new Race()
        def driver1 = new Driver(id: 1L)
    
        driverService.getDriverById(1L) >> driver1
        driverService.getDriverById(2L) >> null  // Covers the `.filter(d -> d != null)`
    
        when:
        def view = controller.saveRace(race, bindingResult, [1L, 2L], model)
    
        then:
        1 * raceService.saveRace({ it.drivers.contains(driver1) && it.drivers.size() == 1 })
        view == "redirect:/races"
    }

    def "saveRace with no drivers selected should save with empty driver set"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> false
        }
        def race = new Race()
    
        when:
        def view = controller.saveRace(race, bindingResult, null, model)
    
        then:
        1 * raceService.saveRace({ it.drivers.empty })
        view == "redirect:/races"
    }

    def "saveRace with empty driver list should save with empty driver set"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> false
        }
        def race = new Race()
    
        when:
        def view = controller.saveRace(race, bindingResult, [], model)
    
        then:
        1 * raceService.saveRace({ it.drivers.empty })
        view == "redirect:/races"
    }

    def "saveRace should print validation errors to console"() {
    given:
    def model = Mock(Model)
    def errorMessage = "Race date is required"
    def mockError = Mock(org.springframework.validation.ObjectError) {
        getDefaultMessage() >> errorMessage
    }
    def bindingResult = Mock(BindingResult) {
        hasErrors() >> true
        getAllErrors() >> [mockError]
    }
    def race = new Race()

    when:
    def view = controller.saveRace(race, bindingResult, [1L, 2L], model)

    then:
    1 * model.addAttribute("teams", _)
    1 * model.addAttribute("drivers", _)
    view == "races/form"
    }
}
