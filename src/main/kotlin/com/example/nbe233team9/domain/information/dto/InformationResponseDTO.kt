package com.example.nbe233team9.domain.information.dto

import com.example.nbe233team9.domain.information.model.Information

data class InformationResponseDTO(
    val id: Long,
    val speciesName: String,
    val breedName: String,
    val picture: String?,
    val age: String,
    val weight: String,
    val height: String,
    val guide: String,
    val description: String,
    val hit: Int
) {
    companion object {
        fun fromEntity(information: Information): InformationResponseDTO {
            return InformationResponseDTO(
                id = information.id!!,
                speciesName = information.breed.species.name,
                breedName = information.breed.name,
                picture = information.picture,
                age = information.age,
                weight = information.weight,
                height = information.height,
                guide = information.guide,
                description = information.description,
                hit = information.hit
            )
        }
    }
}