package com.example.nbe233team9.domain.animal.repository

import com.example.nbe233team9.domain.animal.model.Species
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpeciesRepository : JpaRepository<Species, Long> {
    fun findByName(name: String?): Species?
}