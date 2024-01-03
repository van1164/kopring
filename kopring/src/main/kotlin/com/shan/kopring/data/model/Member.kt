package com.shan.kopring.data.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table (name = "MEMBER")
data class Member (
    @Id
    @Column(name = "ID")
    private val id : String,

    @Column(name = "NAME")
    private val name : String,

    @Column(name = "PASSWORD")
    private val passWord : String
)