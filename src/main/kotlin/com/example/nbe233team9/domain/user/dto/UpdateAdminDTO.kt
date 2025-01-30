package com.example.nbe233team9.domain.user.dto


import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

data class UpdateAdminDTO(
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "비밀번호는 최소 하나의 문자, 숫자, 특수 문자를 포함해야 합니다."
    )
    @Schema(description = "비밀번호", example = "newpassword123!")
    val password: String? = null, // 선택 사항

    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @Schema(description = "관리자 이메일", example = "admin@anicare.com")
    val email: String? = null,

    @Schema(description = "관리자 이름", example = "새로운 관리자 이름")
    val name: String? = null, // 선택 사항

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/new-profile.jpg")
    val profileImg: String? = null // 선택 사항
)
