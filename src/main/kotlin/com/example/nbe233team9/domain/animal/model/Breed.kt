package com.example.nbe233team9.domain.animal.model

import jakarta.persistence.*

@Entity
class Breed(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "species_id")
    var species: Species,

    var name: String
) {
}