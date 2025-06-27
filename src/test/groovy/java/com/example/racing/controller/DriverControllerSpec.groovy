package com.example.racing.controller

import com.example.racing.model.Driver
import com.example.racing.service.DriverService
import com.example.racing.service.TeamService
import com.example.racing.service.RaceService
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import spock.lang.Specification

class DriverControllerSpec extends Specification {

    def driverService = Mock(DriverService)
    def teamService = Mock(TeamService)
    def raceService = Mock(RaceService)
    def controller = new DriverController()

    def setup() {
        controller.driverService = driverService
        controller.teamService = teamService
        controller.raceService = raceService
    }

    def "listDrivers should add drivers to model and return view"() {
        given:
        def model = Mock(Model)
        def drivers = [new Driver()]
        driverService.getAllDrivers() >> drivers

        when:
        def view = controller.listDrivers(model)

        then:
        1 * model.addAttribute("drivers", drivers)
        view == "drivers/list"
    }

    def "showCreateDriverForm should add driver, teams, and races to model"() {
        given:
        def model = Mock(Model)

        when:
        def view = controller.showCreateDriverForm(model)

        then:
        1 * model.addAttribute("driver", _ as Driver)
        1 * model.addAttribute("teams", _)
        1 * model.addAttribute("races", _)
        view == "drivers/form"
    }

    def "saveDriver with validation errors should return form view"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> true
        }
        def driver = new Driver()

        when:
        def view = controller.saveDriver(driver, bindingResult, model)

        then:
        1 * model.addAttribute("teams", _)
        view == "drivers/form"
    }

    def "saveDriver with no errors should save and redirect"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> false
        }
        def driver = new Driver()

        when:
        def view = controller.saveDriver(driver, bindingResult, model)

        then:
        1 * driverService.saveDriver(driver)
        view == "redirect:/drivers"
    }

    def "showEditForm should populate model with driver, teams, and races"() {
        given:
        def model = Mock(Model)
        def driver = new Driver(id: 1L)
        driverService.getDriverById(1L) >> driver

        when:
        def view = controller.showEditForm(1L, model)

        then:
        1 * model.addAttribute("driver", driver)
        1 * model.addAttribute("teams", _)
        1 * model.addAttribute("races", _)
        view == "drivers/form"
    }

    def "updateDriver with validation errors should return form view"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> true
        }
        def driver = new Driver()

        when:
        def view = controller.updateDriver(1L, driver, bindingResult, model)

        then:
        1 * model.addAttribute("teams", _)
        view == "drivers/form"
    }

    def "updateDriver with no errors should update and redirect"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> false
        }
        def driver = new Driver()

        when:
        def view = controller.updateDriver(1L, driver, bindingResult, model)

        then:
        1 * driverService.updateDriver(1L, driver)
        view == "redirect:/drivers"
    }

    def "deleteDriver should delete and redirect if not registered in races"() {
        given:
        def redirectAttributes = Mock(RedirectAttributes)
        def driver = new Driver(registeredRaces: [] as Set)
        driverService.getDriverById(1L) >> driver

        when:
        def view = controller.deleteDriver(1L, redirectAttributes)

        then:
        1 * driverService.deleteDriver(1L)
        view == "redirect:/drivers"
    }

    def "deleteDriver should not delete and show error if registered in races"() {
        given:
        def redirectAttributes = Mock(RedirectAttributes)
        def driver = new Driver(registeredRaces: [new Object()] as Set)
        driverService.getDriverById(2L) >> driver

        when:
        def view = controller.deleteDriver(2L, redirectAttributes)

        then:
        1 * redirectAttributes.addFlashAttribute("error", "Cannot delete driver registered in at least one race.")
        0 * driverService.deleteDriver(_)
        view == "redirect:/drivers"
    }

    def "deleteDriver should delete and redirect if registeredRaces is non-null and empty"() {
        given:
        def redirectAttributes = Mock(RedirectAttributes)
        def driver = new Driver(registeredRaces: Collections.emptySet()) // non-null but empty
        driverService.getDriverById(3L) >> driver
 
        when:
        def view = controller.deleteDriver(3L, redirectAttributes)
 
        then:
        1 * driverService.deleteDriver(3L)
        view == "redirect:/drivers"
    }

    def "deleteDriver should delete and redirect if registeredRaces is null"() {
        given:
        def redirectAttributes = Mock(RedirectAttributes)
        def driver = new Driver(registeredRaces: null) // null case
        driverService.getDriverById(4L) >> driver
    
        when:
        def view = controller.deleteDriver(4L, redirectAttributes)
    
        then:
        1 * driverService.deleteDriver(4L)
        view == "redirect:/drivers"
    }
}

 