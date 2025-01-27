package com.example.nbe233team9.domain.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateAdminDTO(
    @Schema(description = "관리자 이메일", example = "admin@anicare.com")
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @Schema(description = "비밀번호", example = "test!@3!")
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}$",
        message = "비밀번호는 최소 하나의 문자, 숫자, 특수 문자를 포함해야 합니다."
    )
    val password: String,

    @Schema(description = "관리자 이름", example = "관리자")
    @field:NotBlank(message = "이름은 필수 입력 항목입니다.")
    val name: String,

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    val profileImg: String? = null // nullable
)