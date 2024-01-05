package com.shan.kopring.main_page

import com.shan.kopring.EntityManagerObject
import com.shan.kopring.data.model.Member
import com.shan.kopring.data.repository.MemoryMemberRepository
import jakarta.persistence.EntityTransaction
import jakarta.persistence.Persistence
import org.springframework.stereotype.Service

@Service
class MainService {
    private val repository = MemoryMemberRepository()
     val tx: EntityTransaction
        get() = EntityManagerObject.tx
    fun registerMember(member : Member){
        tx.begin()
        repository.save(member)
        tx.commit()
    }

    fun findMemberById(id : String): Member {
        return repository.findById(id)
    }
}