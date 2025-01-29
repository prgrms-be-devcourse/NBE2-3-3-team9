package com.example.nbe233team9.domain.community.dto

data class DetailResponseDTO(
    val community: CommunityResponseDTO,
    val comment: List<CommentResponseDTO>
)
