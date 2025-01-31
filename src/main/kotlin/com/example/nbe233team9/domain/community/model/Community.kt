package com.example.nbe233team9.domain.community.model

import com.example.nbe233team9.common.entities.CommonEntity
import com.example.nbe233team9.domain.community.dto.CommunityRequestDTO
import com.example.nbe233team9.domain.user.model.User
import jakarta.persistence.*

@Entity
class Community(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    var title: String? = null,

    var content: String? = null,

    var picture: String? = null,

    var animalSpecies: String? = null,

    var commentCount: Int = 0,

    var likeCount: Int = 0,

    @OneToMany(mappedBy = "community", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "community", cascade = [CascadeType.ALL], orphanRemoval = true)
    val likes: MutableList<CommunityLike> = mutableListOf()
) : CommonEntity() {

    fun updatePost(requestDTO: CommunityRequestDTO) {
        requestDTO.title.takeIf { !it.isNullOrBlank() }?.let { this.title = it }
        requestDTO.content.takeIf { !it.isNullOrBlank() }?.let { this.content = it }
        requestDTO.animalSpecies.takeIf { !it.isNullOrBlank() }?.let { this.animalSpecies = it }
    }

    fun updatePicture(picture: String) {
        this.picture = picture
    }

    fun updateCommentCount(commentCount: Int) {
        this.commentCount = commentCount
    }

    fun updateLikeCount(likeCount: Int) {
        this.likeCount = likeCount
    }
}