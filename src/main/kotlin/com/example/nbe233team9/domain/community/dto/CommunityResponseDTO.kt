package com.example.nbe233team9.domain.community.dto

import com.example.nbe233team9.domain.community.model.Community
import java.time.LocalDateTime

data class CommunityResponseDTO(
    val id: Long,
    val userId: Long,
    val name: String,
    val profileImg: String,
    val title: String?,
    val content: String?,
    val picture: String?,
    val animalSpecies: String?,
    val commentCount: Int,
    val likeCount: Int,
    var liked: Boolean = false,
    var canEdit: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(community: Community): CommunityResponseDTO {
            return CommunityResponseDTO(
                id = community.id!!,
                userId = community.user.id!!,
                name = community.user.name!!,
                profileImg = community.user.profileImg!!,
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
