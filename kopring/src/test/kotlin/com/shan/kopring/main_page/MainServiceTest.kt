package com.shan.kopring.main_page

import com.shan.kopring.data.model.Member
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class MainServiceTest {
    val service = MainService()

    @Test
    fun registerMember() {
        val member = Member("testId","sihwan","testPW")
        service.registerMember(member)
    }
}