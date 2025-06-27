package com.example.racing.controller
 
import com.example.racing.model.Team
import com.example.racing.service.TeamService
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
 
class TeamControllerSpec extends Specification {
 
    def teamService = Mock(TeamService)
    def controller = new TeamController()
 
    def setup() {
        controller.teamService = teamService
    }
 
    def "listTeams should add teams to model and return view"() {
        given:
        def model = Mock(Model)
        def teams = [new Team(name: "Red Bull")]
        teamService.getAllTeams() >> teams
 
        when:
        def view = controller.listTeams(model)
 
        then:
        1 * model.addAttribute("teams", teams)
        view == "teams/list"
    }
 
    def "showCreateForm should add new team to model and return form view"() {
        given:
        def model = Mock(Model)
 
        when:
        def view = controller.showCreateForm(model)
 
        then:
        1 * model.addAttribute("team", _ as Team)
        view == "teams/form"
    }
 
    def "saveTeam with validation errors should return form view - short name"() {
        given:
        def team = new Team(name: "A")
        def file = Mock(MultipartFile)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> true
        }
        def model = Mock(Model)
 
        when:
        def view = controller.saveTeam(team, file, bindingResult, model)
 
        then:
        1 * model.addAttribute("team", team)
        view == "teams/form"
    }
 
    def "saveTeam with no errors should save and redirect"() {
        given:
        def model = Mock(Model)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> false
        }
        def team = new Team(name: "Ferrari")
        def file = Mock(MultipartFile)
 
        when:
        def view = controller.saveTeam(team, file, bindingResult, model)
 
        then:
        1 * teamService.saveTeamWithLogo(team, file)
        view == "redirect:/teams"
    }
 
    def "showEditForm should add team to model and return form view"() {
        given:
        def model = Mock(Model)
        def team = new Team(id: 1L, name: "Mercedes")
        teamService.getTeamById(1L) >> team
 
        when:
        def view = controller.showEditForm(1L, model)
 
        then:
        1 * model.addAttribute("team", team)
        view == "teams/form"
    }
 
    def "updateTeam with validation errors should return form view"() {
        given:
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> true
        }
        def team = new Team()
        def file = Mock(MultipartFile)
        def model = Mock(Model)
 
        when:
        def view = controller.updateTeam(1L, team, bindingResult, file, model)
 
        then:
        1 * model.addAttribute("team", team)
        view == "teams/form"
    }
 
    def "updateTeam with no errors should update and redirect"() {
        given:
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> false
        }
        def team = new Team(name: "McLaren")
        def file = Mock(MultipartFile)
        def model = Mock(Model)

 
        when:
        def view = controller.updateTeam(1L, team, bindingResult, file, model)
 
        then:
        1 * teamService.updateTeam(1L, team, file)
        view == "redirect:/teams"
    }
 
    def "deleteTeam should delete and redirect"() {
        given:
        def model = Mock(Model)
 
        when:
        def view = controller.deleteTeam(1L, model)
 
        then:
        1 * teamService.deleteTeam(1L)
        view == "redirect:/teams"
    }
 
    def "deleteTeam should handle exception and return list view with error"() {
        given:
        def model = Mock(Model)
        teamService.deleteTeam(2L) >> { throw new IllegalStateException("Cannot delete") }
        def teams = [new Team(name: "Alpine")]
        teamService.getAllTeams() >> teams
 
        when:
        def view = controller.deleteTeam(2L, model)
 
        then:
        1 * model.addAttribute("teams", teams)
        1 * model.addAttribute("error", "Cannot delete")
        view == "teams/list"
    }
 
    def "getTeamLogo should return logo as ResponseEntity"() {
        given:
        def logoBytes = [1, 2, 3] as byte[]
        teamService.getTeamLogoById(1L) >> logoBytes
 
        when:
        ResponseEntity<byte[]> response = controller.getTeamLogo(1L)
 
        then:
        response.statusCode.value() == 200
        response.body == logoBytes
        response.headers.getContentType().toString() == "image/jpeg"
    }
 
    def "saveTeam with overly long name should return form view"() {
        given:
        def team = new Team(name: "A" * 300)
        def file = Mock(MultipartFile)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> true
        }
        def model = Mock(Model)
 
        when:
        def view = controller.saveTeam(team, file, bindingResult, model)
 
        then:
        1 * model.addAttribute("team", team)
        view == "teams/form"
    }
 
    def "updateTeam with overly long name should return form view"() {
        given:
        def team = new Team(name: "A" * 300)
        def model = Mock(Model)
        def file = Mock(MultipartFile)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> true
        }
 
        when:
        def view = controller.updateTeam(1L, team, bindingResult, file, model)
 
        then:
        1 * model.addAttribute("team", team)
        view == "teams/form"
    }
 
    def "updateTeam with valid name should update and redirect"() {
        given:
        def team = new Team(name: "Valid Team")
        def model = Mock(Model)
        def file = Mock(MultipartFile)
        def bindingResult = Mock(BindingResult) {
            hasErrors() >> false
        }
 
        when:
        def view = controller.updateTeam(1L, team, bindingResult, file, model)
 
        then:
        1 * teamService.updateTeam(1L, team, file)
        view == "redirect:/teams"
    }

    def "saveTeam should handle DataIntegrityViolationException and return form view"() {
    given:
    def model = Mock(Model)
    def bindingResult = Mock(BindingResult) {
        hasErrors() >> false
    }
    def team = new Team(name: "Duplicate Team")
    def file = Mock(MultipartFile)

    teamService.saveTeamWithLogo(team, file) >> { throw new DataIntegrityViolationException("Duplicate") }

    when:
    def view = new TeamController(teamService: teamService)
            .saveTeam(team, file, bindingResult, model)

    then:
    1 * bindingResult.rejectValue("name", "error.team", "Team name must be unique.")
    view == "teams/form"
    }

    def "updateTeam should handle DataIntegrityViolationException and return form view"() {
    given:
    def model = Mock(Model)
    def bindingResult = Mock(BindingResult) {
        hasErrors() >> false
    }
    def team = new Team(name: "Duplicate Team")
    def file = Mock(MultipartFile)

    teamService.updateTeam(1L, team, file) >> { throw new DataIntegrityViolationException("Duplicate") }

    when:
    def view = new TeamController(teamService: teamService)
            .updateTeam(1L, team, bindingResult, file, model)

    then:
    1 * bindingResult.rejectValue("name", "error.team", "Team name must be unique.")
    1 * model.addAttribute("team", team)
    view == "teams/form"
    }

    def "updateTeam should reject empty team name"() {
    given:
    def model = Mock(Model)
    def bindingResult = Mock(BindingResult) {
        hasErrors() >> true
    }
    def team = new Team(name: "   ") // blank name
    def file = Mock(MultipartFile)
    def controller = new TeamController(teamService: teamService)

    when:
    def view = controller.updateTeam(1L, team, bindingResult, file, model)

    then:
    1 * bindingResult.rejectValue("name", "error.team", "Team name is required.")
    1 * model.addAttribute("team", team)
    view == "teams/form"
    }


    def "disableAutoValidation should set validator to null"() {
    given:
    def binder = Mock(WebDataBinder)

    when:
    new TeamController().disableAutoValidation(binder)

    then:
    1 * binder.setValidator(null)
    }
}
 