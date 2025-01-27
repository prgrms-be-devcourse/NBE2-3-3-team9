package com.example.nbe233team9.domain.pet.dto

import lombok.Getter
import java.time.LocalDateTime

class PetDTO {
    private val id: Long? = null
    private val userId: Long? = null
    private val speciesId: Long? = null
    private val breedId: Long? = null
    private val name: String? = null
    private val age: String? = null
    private val picture: String? = null
    private val gender: String? = null
    private val speciesName: String? = null
    private val breedName: String? = null
    private val createdAt: LocalDateTime? = null
    private val updatedAt: LocalDateTime? = null

    @Getter
    class AddPetDTO {
        private val breedId: Long? = null
        private val speciesId: Long? = null
        private val name: String? = null
        private val age: String? = null
        private val gender: String? = null
    }

    @Getter
    class UpdatePetDTO {
        private val id: Long? = null
        private val breedId: Long? = null
        private val speciesId: Long? = null
        private val name: String? = null
        private val age: String? = null
        private val gender: String? = null
    }
}