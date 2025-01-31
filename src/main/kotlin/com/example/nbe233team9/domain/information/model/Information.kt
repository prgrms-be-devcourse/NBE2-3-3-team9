package com.example.nbe233team9.domain.information.model

import com.example.nbe233team9.domain.animal.model.Breed
import com.example.nbe233team9.domain.community.dto.CommunityRequestDTO
import com.example.nbe233team9.domain.information.dto.InformationRequestDTO
import jakarta.persistence.*

@Entity
class Information(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "breed_id")
    val breed: Breed,

    var picture: String? = null,
    var age: String? = null,
    var weight: String? = null,
    var height: String? = null,
    var guide: String? = null,
    var description: String? = null,
    var hit: Int = 0
) {

    fun updateHit(hit: Int) {
        this.hit = hit
    }

    fun updateInformation(requestDTO: InformationRequestDTO) {
        requestDTO.age.takeIf { !it.isNullOrBlank() }?.let { this.age = it }
        requestDTO.weight.takeIf { !it.isNullOrBlank() }?.let { this.weight = it }
        requestDTO.height.takeIf { !it.isNullOrBlank() }?.let { this.height = it }
        requestDTO.guide.takeIf { !it.isNullOrBlank() }?.let { this.guide = it }
        requestDTO.description.takeIf { !it.isNullOrBlank() }?.let { this.description = it }
    }

    fun updatePicture(picture: String) {
        this.picture = picture
    }
}
