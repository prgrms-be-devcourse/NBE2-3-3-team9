package com.example.nbe233team9.domain.community.dto

import com.example.nbe233team9.domain.community.model.Comment
import java.time.LocalDateTime

data class CommentResponseDTO(
    val id: Long,
    val communityId: Long,
    val userId: Long,
    val name: String,
    val profileImg: String?,
    val content: String,
    var canEdit: Boolean = false,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null
) {
    companion object {
        fun fromEntity(comment: Comment): CommentResponseDTO {
            return CommentResponseDTO(
                id = comment.id!!,
                communityId = comment.community.id!!,
                userId = comment.user.id!!,
                name = comment.user.name!!,
                profileImg = comment.user.profileImg,
                content = comment.content,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt
            )
        }
    }
}