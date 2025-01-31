package com.example.nbe233team9.domain.information.model

import com.example.nbe233team9.domain.animal.model.Breed
import jakarta.persistence.*

@Entity
class Information(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "breed_id")
    val breed: Breed,

    val picture: String?,
    val age: String,
    val weight: String,
    val height: String,
    val guide: String,
    val description: String,
    var hit: Int = 0
) {
    fun updateHit(hit: Int) {
        this.hit = hit
    }
}
