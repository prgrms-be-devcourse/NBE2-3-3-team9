package com.example.nbe233team9.domain.animal.dto

import com.example.nbe233team9.domain.animal.model.Breed
import com.example.nbe233team9.domain.animal.model.Species


class FindBreedDTO(
    val id: Long,
    val speciesId: Long,
    val name: String
) {
    companion object {
        fun fromEntity(breed: Breed): FindBreedDTO {
            return FindBreedDTO(
                id = breed.id,
                speciesId = breed.species.id,
                name = breed.name
            )
        }
    }
}
