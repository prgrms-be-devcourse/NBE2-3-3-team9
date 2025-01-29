package com.example.nbe233team9.domain.community.dto

import com.example.nbe233team9.domain.community.model.Community
import java.time.LocalDateTime

data class CommunityResponseDTO(
    var id: Long? = null,
    var userId: Long? = null,
    var name: String? = null,
    var profileImg: String? = null,
    var title: String? = null,
    var content: String? = null,
    var picture: String? = null,
    var animalSpecies: String? = null,
    var commentCount: Int = 0,
    var likeCount: Int = 0,
    var liked: Boolean = false,
    var canEdit: Boolean = false,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null
) {
    companion object {
        fun fromEntity(community: Community): CommunityResponseDTO {
            return CommunityResponseDTO(
                id = community.id,
                userId = community.user.id,
                name = community.user.name,
                profileImg = community.user.profileImg,
                title = community.title,
                content = community.content,
                picture = community.picture,
                animalSpecies = community.animalSpecies,
                commentCount = community.commentCount,
                likeCount = community.likeCount,
                createdAt = community.createdAt,
                updatedAt = community.updatedAt
            )
        }
    }
}
