package com.example.nbe233team9.domain.pet.model

import com.example.nbe233team9.common.entities.CommonEntity
import com.example.nbe233team9.domain.animal.model.Breed
import com.example.nbe233team9.domain.animal.model.Species
import com.example.nbe233team9.domain.animal.repository.BreedRepository
import com.example.nbe233team9.domain.animal.repository.SpeciesRepository
import com.example.nbe233team9.domain.pet.dto.PetDTO
import com.example.nbe233team9.domain.user.model.User
import jakarta.persistence.*

@Entity
class Pet (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne
    @JoinColumn(name = "species_id", nullable = false)
    var species: Species,

    @ManyToOne
    @JoinColumn(name = "breed_id", nullable = false)
    var breed: Breed,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var age: String? = null,

    @Column(nullable = true)
    var picture: String? = null,

    @Column(nullable = false)
    var gender: String
) : CommonEntity() {


    fun updatePet(
        request: PetDTO.UpdatePetDTO,
        spRepo: SpeciesRepository,
        brRepo: BreedRepository,
        picture: String?
    ): Pet {
        this.species = spRepo.findById(request.speciesId).orElseThrow { RuntimeException() }
        this.breed = brRepo.findById(request.breedId).orElseThrow { RuntimeException() }
        this.name = request.name
        this.age = request.age
        this.gender = request.gender
        this.picture = picture
        return this
    }
}