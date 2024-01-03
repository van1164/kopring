package com.shan.kopring.data.repository

import com.shan.kopring.data.model.Member
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityTransaction

interface  MemberRepository {
    val em :EntityManager
    val tx : EntityTransaction
    fun save(member: Member)
    fun findById(id : String) : Any
}