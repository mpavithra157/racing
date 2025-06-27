package com.example.racing.model

import spock.lang.Specification
import java.time.LocalDate

class DriverSpec extends Specification {

    def "should set and get all fields correctly"() {
        given:
        def driver = new Driver()
        def team = new Team()
        def races = [new Race()] as Set

        when:
        driver.setFirstName("Lewis")
        driver.setLastName("Hamilton")
        driver.setDob(LocalDate.of(1985, 1, 7))
        driver.setTeam(team)
        driver.setRegisteredRaces(races)

        then:
        driver.getFirstName() == "Lewis"
        driver.getLastName() == "Hamilton"
        driver.getDob() == LocalDate.of(1985, 1, 7)
        driver.getTeam() == team
        driver.getRegisteredRaces() == races
    }

    def "toString should return id as string"() {
        given:
        def driver = new Driver()
        driver.@id = 44L

        expect:
        driver.toString() == "44"
    }

    def "should initialize registeredRaces set"() {
        when:
        def driver = new Driver()

        then:
        driver.getRegisteredRaces() != null
        driver.getRegisteredRaces().isEmpty()
    }

    def "getId should return driver's ID"() {
    given:
    def driver = new Driver()
    driver.setId(42L)

    expect:
    driver.getId() == 42L
    }
}

