package com.shan.kopring.main_page

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody


@Controller
class MainController {
    val service = MainService()
    @GetMapping("")
    @ResponseBody
    fun mainGetMap(model : Model): String {
        return "test"
    }
}