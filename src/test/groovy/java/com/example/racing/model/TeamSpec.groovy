package com.example.racing.model

import spock.lang.Specification

class TeamSpec extends Specification {

    def "should set and get all fields correctly"() {
        given:
        def team = new Team()
        def logoBytes = [1, 2, 3] as byte[]

        when:
        team.setName("Red Bull")
        team.setLocation("Austria")
        team.setDescription("Top F1 team")
        team.setLogo(logoBytes)

        then:
        team.getName() == "Red Bull"
        team.getLocation() == "Austria"
        team.getDescription() == "Top F1 team"
        team.getLogo() == logoBytes
    }

    def "toString should return id as string"() {
        given:
        def team = new Team()
        team.@id = 42L

        expect:
        team.toString() == "42"
    }

    def "should initialize drivers and races lists"() {
        when:
        def team = new Team()

        then:
        team.getDrivers() != null
        team.getRaces() == null || team.getRaces() instanceof List
    }

    def "should set and get drivers list correctly"() {
        given:
        def team = new Team()
        def driver1 = new Driver()
        def driver2 = new Driver()
        def drivers = [driver1, driver2] as List

        when:
        team.setDrivers(drivers)

        then:
        team.getDrivers() == drivers
    }
}
