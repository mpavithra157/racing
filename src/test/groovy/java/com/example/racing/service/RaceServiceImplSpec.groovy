package com.example.racing.service.impl
 
import com.example.racing.model.Race
import com.example.racing.repository.RaceRepository
import spock.lang.Specification
 
class RaceServiceImplSpec extends Specification {
 
    RaceRepository raceRepository = Mock()
    RaceServiceImpl raceService = new RaceServiceImpl(raceRepository)
 
    def "should return all races"() {
        given:
        List<Race> races = [new Race(), new Race()]
        raceRepository.findAll() >> races
 
        when:
        def result = raceService.getAllRaces()
 
        then:
        result == races
    }
 
    def "should return race by ID if found"() {
        given:
        Race race = new Race(id: 1L)
        raceRepository.findById(1L) >> Optional.of(race)
 
        when:
        def result = raceService.getRaceById(1L)
 
        then:
        result == race
    }
 
    def "should throw exception if race not found by ID"() {
        given:
        raceRepository.findById(1L) >> Optional.empty()
 
        when:
        raceService.getRaceById(1L)
 
        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Race not found with id: 1"
    }
 
    def "should save a race"() {
        given:
        Race race = new Race()
 
        when:
        raceService.saveRace(race)
 
        then:
        1 * raceRepository.save(race)
    }
 
    def "should update a race if exists"() {
        given:
        Race updatedRace = new Race(id: null, raceTrackName: "Updated")
        raceRepository.existsById(1L) >> true
 
        when:
        raceService.updateRace(1L, updatedRace)
 
        then:
        1 * raceRepository.save({ it.id == 1L && it.raceTrackName == "Updated" })
    }
 
    def "should throw exception when updating non-existent race"() {
        given:
        Race updatedRace = new Race()
        raceRepository.existsById(1L) >> false
 
        when:
        raceService.updateRace(1L, updatedRace)
 
        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Cannot update. Race not found with id: 1"
    }
 
    def "should delete race if it exists"() {
        given:
        raceRepository.existsById(1L) >> true
 
        when:
        raceService.deleteRace(1L)
 
        then:
        1 * raceRepository.deleteById(1L)
    }
 
    def "should throw exception when deleting non-existent race"() {
        given:
        raceRepository.existsById(1L) >> false
 
        when:
        raceService.deleteRace(1L)
 
        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Cannot delete. Race not found with id: 1"
    }
}
 