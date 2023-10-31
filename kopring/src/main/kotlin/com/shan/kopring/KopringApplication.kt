package com.shan.kopring

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication


@SpringBootApplication(exclude= [DataSourceAutoConfiguration::class])
class KopringApplication
fun main(args: Array<String>) {
	val emf = Persistence.createEntityManagerFactory("jpaTest")
	val em = emf.createEntityManager()
	val tx = em.transaction
	try {
		tx.begin()
		logic(em)
		tx.commit()
	} catch (e: Exception) {
		tx.rollback()
	} finally {
		em.close()
	}
	emf.close()

	runApplication<KopringApplication>(*args)



}
private fun logic(em: EntityManager){

}