package com.shan.kopring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication


@SpringBootApplication(exclude= [DataSourceAutoConfiguration::class])
class KopringApplication

fun main(args: Array<String>) {
	runApplication<KopringApplication>(*args)
}
