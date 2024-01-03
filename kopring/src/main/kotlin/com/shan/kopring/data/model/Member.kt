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
    val id : String,

    @Column(name = "NAME")
    val name : String,

    @Column(name = "PASSWORD")
    val passWord : String
)