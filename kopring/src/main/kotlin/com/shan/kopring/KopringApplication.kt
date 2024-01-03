package com.shan.kopring

import com.shan.kopring.data.model.Member
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.service.ServiceRegistry
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
@EntityScan(basePackages = ["com.shan.kopring.data.model.Member"])
class KopringApplication
fun main(args: Array<String>) {
	val configuration = Configuration()
	configuration.addAnnotatedClass(Member::class.java)
	val serviceRegistry: ServiceRegistry =
		StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build()
	val sessionFactory = configuration.buildSessionFactory(serviceRegistry)

	runApplication<KopringApplication>(*args)
}