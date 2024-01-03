package com.shan.kopring

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.EntityTransaction
import jakarta.persistence.Persistence
import jakarta.persistence.PersistenceUnit

object EntityManagerObject {
    val emf: EntityManagerFactory = Persistence.createEntityManagerFactory("jpaTest")
    val em: EntityManager = emf.createEntityManager()
    val tx: EntityTransaction = em.transaction
}