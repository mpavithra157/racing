package com.example.racing.model

import spock.lang.Specification
import java.time.LocalDate

class RaceSpec extends Specification {

    def "should set and get all fields correctly"() {
        given:
        def race = new Race()
        def team = new Team()
        def drivers = [new Driver()] as Set

        when:
        race.setRaceTrackName("Silverstone")
        race.setLocation("UK")
        race.setDate(LocalDate.of(2023, 7, 18))
        race.setRegistrationClosureDate(LocalDate.of(2023, 7, 1))
        race.setTeam(team)
        race.setDrivers(drivers)

        then:
        race.getRaceTrackName() == "Silverstone"
        race.getLocation() == "UK"
        race.getDate() == LocalDate.of(2023, 7, 18)
        race.getRegistrationClosureDate() == LocalDate.of(2023, 7, 1)
        race.getTeam() == team
        race.getDrivers() == drivers
    }

    def "should initialize drivers set"() {
        when:
        def race = new Race()

        then:
        race.getDrivers() != null
        race.getDrivers().isEmpty()
    }

    def "toString should return string representation of id"() {
        given:
        def race = new Race()
        race.setId(99L)
        expect:
        race.toString() == "99"
    }
}

