package com.example.nbe233team9.domain.user.dto

import io.swagger.v3.oas.annotations.media.Schema

data class UpdateUserDTO(
    @Schema(description = "유저이름", example = "유저이름")
    val name: String,

    @Schema(description = "반려동물경험")
    val years_of_experience: Int = 0
)
