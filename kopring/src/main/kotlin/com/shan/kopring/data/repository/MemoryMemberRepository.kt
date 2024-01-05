package com.shan.kopring.data.repository

import com.shan.kopring.EntityManagerObject
import com.shan.kopring.data.model.Member
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityTransaction

class MemoryMemberRepository : MemberRepository {

    override val em: EntityManager
        get() = EntityManagerObject.em


    override fun save(member: Member) {
        em.persist(member)
    }

    override fun findById(id: String): Member {
        return em.find(Member::class.java, id)
    }

}