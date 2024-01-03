package com.shan.kopring.main_page

import com.shan.kopring.EntityManagerObject
import com.shan.kopring.data.model.Member
import com.shan.kopring.data.repository.MemoryMemberRepository
import jakarta.persistence.Persistence
import org.springframework.stereotype.Service

@Service
class MainService {
    private val repository = MemoryMemberRepository()

    fun registerMember(member : Member){
        repository.save(member)
    }

    fun findMemberById(id : String): Member {
        return repository.findById(id)
    }
}