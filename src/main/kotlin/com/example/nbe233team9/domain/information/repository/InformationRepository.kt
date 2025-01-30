package com.example.nbe233team9.domain.information.repository

import com.example.nbe233team9.domain.information.model.Information
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface InformationRepository : JpaRepository<Information, Long> {

    @Query("SELECT i FROM Information i")
    fun findAllInformation(pageable: Pageable): Page<Information>

    @Query("SELECT i FROM Information i WHERE i.breed.species.name = :speciesName")
    fun findBySpeciesName(speciesName: String, pageable: Pageable): Page<Information>

    @Query("SELECT i FROM Information i JOIN i.breed b JOIN b.species s WHERE s.name = :speciesName AND LOWER(b.name) LIKE LOWER(CONCAT('%', :breedName, '%'))")
    fun findBySpeciesAndBreed(speciesName: String, breedName: String, pageable: Pageable): Page<Information>

    @Query("SELECT i FROM Information i JOIN i.breed b JOIN b.species s WHERE s.name = :speciesName AND b.name = :breedName")
    fun findBySpeciesAndBreed(speciesName: String, breedName: String): Information?
}