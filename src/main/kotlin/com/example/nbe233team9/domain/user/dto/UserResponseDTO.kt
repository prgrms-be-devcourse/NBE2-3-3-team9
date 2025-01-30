package com.example.nbe233team9.domain.user.dto

import com.example.nbe233team9.domain.user.model.Role


data class UserResponseDTO(
    val id: Long,
    val nickname: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
    val profileImg: String,
    val role: Role
)
