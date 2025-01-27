package com.example.nbe233team9.domain.pet.model

import jakarta.persistence.*

@Entity
class Pet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val speciesId: Long,

    @Column(nullable = false)
    val breedId: Long,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true)
    val age: String? = null,

    @Column(nullable = true)
    val picture: String? = null,

    @Column(nullable = false)
    val gender: String
) {


//    fun updatePet(
//        request: PetDTO.UpdatePetDTO,
//        spRepo: SpeciesRepository,
//        brRepo: BreedRepository,
//        Picture: String?
//    ): Pet {
//        this.species = spRepo.findById(request.getSpeciesId()).orElseThrow { RuntimeException() }
//        this.breed = brRepo.findById(request.getBreedId()).orElseThrow { RuntimeException() }
//        this.name = request.getName()
//        this.age = request.getAge()
//        this.gender = request.getGender()
//        this.picture = Picture
//        return this
//    }
}