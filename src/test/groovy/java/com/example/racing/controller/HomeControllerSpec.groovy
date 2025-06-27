package com.example.racing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@WebMvcTest(HomeController)
class HomeControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    def "should return index view when accessing root URL"() {
        expect:
        mockMvc.perform(get("/"))
               .andExpect(view().name("index"))
    }
}
