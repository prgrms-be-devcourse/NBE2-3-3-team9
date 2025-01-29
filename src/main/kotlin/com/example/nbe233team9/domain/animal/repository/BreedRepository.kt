package com.example.nbe233team9.domain.animal.repository

import com.example.nbe233team9.domain.animal.model.Breed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BreedRepository : JpaRepository<Breed, Long> {
}