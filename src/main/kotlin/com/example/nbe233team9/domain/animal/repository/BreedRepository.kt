package com.example.nbe233team9.domain.animal.repository

import com.example.nbe233team9.domain.animal.model.Breed
import com.example.nbe233team9.domain.animal.model.Species
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BreedRepository : JpaRepository<Breed, Long> {
    fun existsByNameAndSpecies(name: String, species: Species): Boolean

    fun findBreedsBySpecies(species: Species): List<Breed>

    fun findByName(name: String): Breed
}