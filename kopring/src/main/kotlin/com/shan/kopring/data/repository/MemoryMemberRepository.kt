package com.shan.kopring.data.repository

import com.shan.kopring.EntityManagerObject
import com.shan.kopring.data.model.Member
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityTransaction

class MemoryMemberRepository : MemberRepository {

    override val em: EntityManager
        get() = EntityManagerObject.em
    override val tx: EntityTransaction
        get() = EntityManagerObject.tx

    override fun save(member: Member) {
        tx.begin()
        em.persist(member)
        tx.commit()
    }

    override fun findById(id: String): Member {
        val member = em.find(Member::class.java,id)
        return member
    }

}