package com.example.nbe233team9.domain.pet.dto

import com.example.nbe233team9.domain.pet.model.Pet
import java.time.LocalDateTime

class PetDTO(
    val id: Long,
    val userId: Long,
    val speciesId: Long,
    val breedId: Long,
    val name: String,
    val age: String? = null,
    val picture: String? = null,
    val gender: String,
    val speciesName: String? = null,
    val breedName: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

    class AddPetDTO(
        val breedId: Long,
        val speciesId: Long,
        val name: String,
        val age: String? = null,
        val gender: String
    ) {
    }

    class UpdatePetDTO(
        val id: Long,
        val breedId: Long,
        val speciesId: Long,
        val name: String,
        val age: String? = null,
        val gender: String
    ) {
    }

    companion object {
        fun fromEntity(pet: Pet): PetDTO {
            return PetDTO(
                id = pet.id,
                userId = pet.user.id!!,
                speciesId = pet.species.id,
                breedId = pet.breed.id,
                name = pet.name,
                age = pet.age,
                picture = pet.picture,
                gender = pet.gender,
                speciesName = pet.species.name,
                breedName = pet.breed.name,
                createdAt = pet.createdAt,
                updatedAt = pet.updatedAt
            )
        }
    }
}
