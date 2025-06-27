package com.example.racing.service.impl
 
import com.example.racing.model.Driver
import com.example.racing.model.Race
import com.example.racing.repository.DriverRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
 
class DriverServiceImplSpec extends Specification {
 
    DriverRepository driverRepository = Mock()
    DriverServiceImpl driverService = new DriverServiceImpl(driverRepository: driverRepository)
 
    def "should get all drivers"() {
        given:
        def driverList = [new Driver(), new Driver()]
        driverRepository.findAll() >> driverList
 
        when:
        def result = driverService.getAllDrivers()
 
        then:
        result == driverList
    }
 
    def "should get driver by ID if exists"() {
        given:
        def driver = new Driver(id: 1L)
        driverRepository.findById(1L) >> Optional.of(driver)
 
        when:
        def result = driverService.getDriverById(1L)
 
        then:
        result == driver
    }
 
    def "should return null when driver not found by ID"() {
        given:
        driverRepository.findById(2L) >> Optional.empty()
 
        when:
        def result = driverService.getDriverById(2L)
 
        then:
        result == null
    }
 
    def "should save a driver"() {
        given:
        def driver = new Driver()
 
        when:
        driverService.saveDriver(driver)
 
        then:
        1 * driverRepository.save(driver)
    }
 
    def "should update existing driver"() {
        given:
        def existingDriver = new Driver(id: 1L, firstName: "Old", registeredRaces: [])
        def updatedDriver = new Driver(firstName: "New", registeredRaces: [new Race()])
        driverRepository.findById(1L) >> Optional.of(existingDriver)
 
        when:
        driverService.updateDriver(1L, updatedDriver)
 
        then:
        1 * driverRepository.save({
            it.firstName == "New" &&
            it.registeredRaces == updatedDriver.registeredRaces
        })
    }
 
    def "should not update if driver not found"() {
        given:
        def updatedDriver = new Driver()
        driverRepository.findById(3L) >> Optional.empty()
 
        when:
        driverService.updateDriver(3L, updatedDriver)
 
        then:
        0 * driverRepository.save(_)
    }
 
    def "should delete driver if no registered races"() {
        given:
        def driver = new Driver(id: 1L, registeredRaces: [])
        driverService.driverRepository = driverRepository
        1 * driverRepository.findById(1L) >> Optional.of(driver)
 
        when:
        driverService.deleteDriver(1L)
 
        then:
        1 * driverRepository.deleteById(1L)
    }
 
    def "should throw exception if driver has registered races"() {
        given:
        def driver = new Driver(id: 1L, registeredRaces: [new Race()])
        1 * driverRepository.findById(1L) >> Optional.of(driver)
 
        when:
        driverService.deleteDriver(1L)
 
        then:
        def ex = thrown(IllegalStateException)
        ex.message == "Driver cannot be deleted. They have registered races."
        0 * driverRepository.deleteById(_)
    }
    
    def "should delete driver if registeredRaces is null"() {
        given:
        def driver = new Driver(id: 2L, registeredRaces: null)
        1 * driverRepository.findById(2L) >> Optional.of(driver)
    
        when:
        driverService.deleteDriver(2L)
    
        then:
        1 * driverRepository.deleteById(2L)
    }

    def "should throw exception if driver is null"() {
        given:
        driverRepository.findById(99L) >> Optional.empty()  // Simulate not found
    
        when:
        driverService.deleteDriver(99L)
    
        then:
        def ex = thrown(IllegalStateException)
        ex.message == "Driver cannot be deleted. They have registered races."
        0 * driverRepository.deleteById(_)
    }
}
