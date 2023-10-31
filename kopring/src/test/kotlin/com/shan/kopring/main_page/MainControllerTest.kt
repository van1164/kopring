package com.shan.kopring.main_page

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainControllerTest(@Autowired val restTemplate: TestRestTemplate) {

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun mainGetMap() {
        println(">> Assert getMapping()")
        val entity = restTemplate.getForEntity<String>("/")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("test")
    }
}