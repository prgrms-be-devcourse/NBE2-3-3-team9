package com.example.nbe233team9.domain.user.dto

import java.time.LocalDateTime

data class UserDetailResponseDTO(
    val id: Long,
    val name: String,
    val email: String,
    val profileImg: String?,
    val years_of_experience: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
