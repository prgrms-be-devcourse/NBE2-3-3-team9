package com.example.nbe233team9.domain.animal.dto

import com.example.nbe233team9.domain.animal.model.Species


class FindSpeciesDTO(
    val id: Long,
    val name: String
) {
    companion object {
        fun fromEntity(species: Species): FindSpeciesDTO {
            return FindSpeciesDTO(
                id = species.id,
                name = species.name
            )
        }
    }
}
