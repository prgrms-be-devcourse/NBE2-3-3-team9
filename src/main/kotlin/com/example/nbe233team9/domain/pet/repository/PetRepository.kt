package com.example.nbe233team9.domain.pet.repository

import com.example.nbe233team9.domain.pet.model.Pet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PetRepository : JpaRepository<Pet?, Long?> {
    fun findAllByUserId(userId: Long?): List<Pet?>?
}
