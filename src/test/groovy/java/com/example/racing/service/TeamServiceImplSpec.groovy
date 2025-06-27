package com.example.racing.service.impl

import com.example.racing.exception.FileSizeExceededException
import com.example.racing.exception.LogoMissingException
import com.example.racing.model.Race
import com.example.racing.model.Team
import com.example.racing.repository.TeamRepository
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
import spock.lang.Subject

class TeamServiceImplSpec extends Specification {

    @Subject
    TeamServiceImpl teamService

    TeamRepository teamRepository = Mock()

    def setup() {
        teamService = new TeamServiceImpl(teamRepository)
    }

    def "getAllTeams should return all teams"() {
        given:
        def teams = [new Team(), new Team()]
        teamRepository.findAll() >> teams
 
        when:
        def result = teamService.getAllTeams()
 
        then:
        result.size() == 2
    }
 
    def "getTeamById should return team if found"() {
        given:
        def team = new Team(id: 1)
        teamRepository.findById(1L) >> Optional.of(team)
 
        when:
        def result = teamService.getTeamById(1L)
 
        then:
        result == team
    }
 
    def "getTeamById should throw exception if not found"() {
        given:
        teamRepository.findById(1L) >> Optional.empty()
 
        when:
        teamService.getTeamById(1L)
 
        then:
        def e = thrown(RuntimeException)
        e.message == "Team not found"
    }
 
    def "saveTeamWithLogo should save new team with logo"() {
        given:
        def team = new Team()
        team.metaClass.getId = { null } // for new team
        byte[] bytes = "logo".bytes
        MultipartFile mockFile = Mock()
        mockFile.isEmpty() >> false
        mockFile.getSize() >> 1024
        mockFile.getBytes() >> bytes
 
        when:
        teamService.saveTeamWithLogo(team, mockFile)
 
        then:
        1 * teamRepository.save(_ as Team)
        team.logo == bytes
    }
 
    def "saveTeamWithLogo should throw LogoMissingException if logo is missing for new team"() {
        given:
        def team = new Team()
        team.metaClass.getId = { null } // for new team
        MultipartFile mockFile = Mock()
        mockFile.isEmpty() >> true
 
        when:
        teamService.saveTeamWithLogo(team, mockFile)
 
        then:
        thrown(LogoMissingException)
    }

    def "saveTeamWithLogo should throw FileSizeExceededException if logo size exceeds 50KB"() {
        given:
        def team = new Team()
        team.metaClass.getId = { null }
        MultipartFile mockFile = Mock() {
            isEmpty() >> false
            getSize() >> 60000
        }

        when:
        teamService.saveTeamWithLogo(team, mockFile)

        then:
        thrown(FileSizeExceededException)
    }

    def "updateTeam should update and save existing team with new details"() {
        given:
        def existing = new Team()
        existing.metaClass.getId = { 1L }
        existing.logo = "old".bytes
        def updated = new Team(name: "New", location: "NewLoc", description: "NewDesc")
        byte[] newLogo = "newlogo".bytes
        MultipartFile mockFile = Mock() {
            isEmpty() >> false
            getSize() >> 1024
            getBytes() >> newLogo
        }

        teamRepository.findById(1L) >> Optional.of(existing)

        when:
        teamService.updateTeam(1L, updated, mockFile)

        then:
        1 * teamRepository.save(_ as Team)
        existing.name == "New"
        existing.location == "NewLoc"
        existing.description == "NewDesc"
        existing.logo == newLogo
    }

    def "updateTeam should throw FileSizeExceededException if file exceeds 50KB"() {
        given:
        def existing = new Team()
        existing.metaClass.getId = { 1L }
        def updated = new Team(name: "Updated")
        MultipartFile mockFile = Mock() {
            isEmpty() >> false
            getSize() >> 60000
        }

        teamRepository.findById(1L) >> Optional.of(existing)

        when:
        teamService.updateTeam(1L, updated, mockFile)

        then:
        thrown(FileSizeExceededException)
    }

    def "updateTeam should throw exception if team not found"() {
        given:
        teamRepository.findById(1L) >> Optional.empty()

        when:
        teamService.updateTeam(1L, new Team(), null)

        then:
        thrown(RuntimeException)
    }

    def "deleteTeam should delete team if not associated with race"() {
        given:
        def team = new Team()
        team.metaClass.getId = { 1L }
        team.metaClass.getRaces = { [] as Set }
        teamRepository.findById(1L) >> Optional.of(team)

        when:
        teamService.deleteTeam(1L)

        then:
        1 * teamRepository.delete(team)
    }

    def "deleteTeam should throw exception if team is in a race"() {
        given:
        def team = new Team()
        team.setRaces([new Race()])
        teamRepository.findById(1L) >> Optional.of(team)

        when:
        teamService.deleteTeam(1L)

        then:
        def e = thrown(IllegalStateException)
        e.message == "Cannot delete team registered in at least one race."
    }

    def "getTeamLogoById should return logo bytes"() {
        given:
        def logoBytes = "image".bytes
        def team = new Team()
        team.metaClass.getId = { 1L }
        team.logo = logoBytes
        teamRepository.findById(1L) >> Optional.of(team)

        when:
        def result = teamService.getTeamLogoById(1L)

        then:
        result == logoBytes
    }

    def "getTeamLogoById should throw exception if not found"() {
        given:
        teamRepository.findById(1L) >> Optional.empty()

        when:
        teamService.getTeamLogoById(1L)

        then:
        thrown(RuntimeException)
    }

    def "saveTeamWithLogo should print file empty status when logofile is null"() {
        given:
        def team = new Team()
        team.metaClass.getId = { null }

        when:
        teamService.saveTeamWithLogo(team, null)

        then:
        def e = thrown(LogoMissingException)
        e.message == "Logo is mandatory"
    }

    def "saveTeamWithLogo should print file empty status when logofile is empty"() {
        given:
        def team = new Team()
        team.metaClass.getId = { null }
        def file = Mock(MultipartFile) {
            isEmpty() >> true
        }

        when:
        teamService.saveTeamWithLogo(team, file)

        then:
        def e = thrown(LogoMissingException)
        e.message == "Logo is mandatory"
    }

    def "saveTeamWithLogo should skip saving if team is not new"() {
        given:
        def team = new Team(id: 1L)
        def file = Mock(MultipartFile)

        when:
        teamService.saveTeamWithLogo(team, file)

        then:
        0 * teamRepository.save(_)
    }

    def "saveTeamWithLogo should throw exception if logo file exceeds size limit"() {
        given:
        def team = new Team()
        team.metaClass.getId = { null }
        def file = Mock(MultipartFile) {
            isEmpty() >> false
            getSize() >> 60000
        }

        when:
        teamService.saveTeamWithLogo(team, file)

        then:
        def e = thrown(FileSizeExceededException)
        e.message == "Logo file size exceeds 50KB"
    }

    def "updateTeam should throw exception if update file exceeds size limit"() {
        given:
        def existingTeam = new Team()
        teamRepository.findById(1L) >> Optional.of(existingTeam)
        def updatedTeam = new Team(name: "Updated", location: "City", description: "Desc")
        def file = Mock(MultipartFile) {
            isEmpty() >> false
            getSize() >> 60000
        }

        when:
        teamService.updateTeam(1L, updatedTeam, file)

        then:
        def e = thrown(FileSizeExceededException)
        e.message == "Logo file size exceeds 50KB"
    }

    def "getTeamById should throw exception if team not found"() {
        given:
        teamRepository.findById(99L) >> Optional.empty()

        when:
        teamService.getTeamById(99L)

        then:
        def e = thrown(RuntimeException)
        e.message == "Team not found"
    }

    def "deleteTeam should throw exception if team has races"() {
        given:
        def team = new Team()
        team.setRaces([new Race()])
        teamRepository.findById(1L) >> Optional.of(team)

        when:
        teamService.deleteTeam(1L)

        then:
        def e = thrown(IllegalStateException)
        e.message == "Cannot delete team registered in at least one race."
    }

}
