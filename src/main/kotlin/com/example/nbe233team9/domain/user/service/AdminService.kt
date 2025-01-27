package com.example.nbe233team9.domain.user.service

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.domain.user.dto.AdminResponseDTO
import com.example.nbe233team9.domain.user.dto.CreateAdminDTO
import com.example.nbe233team9.domain.user.model.Role
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.repository.UserRepository
import lombok.AllArgsConstructor
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AdminService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun createAdmin(createAdminDTO: CreateAdminDTO): AdminResponseDTO {
        if (userRepository.existsByEmail(createAdminDTO.email)) {
            throw CustomException(ResultCode.EMAIL_ALREADY_EXISTS)
        }

        val encodedPassword = passwordEncoder.encode(createAdminDTO.password)

        val user = User(
            email = createAdminDTO.email,
            name = createAdminDTO.name,
            password = encodedPassword,
            profileImg = createAdminDTO.profileImg, // 기본값 제공
            role = Role.ADMIN
        )

        userRepository.save(user)

        return AdminResponseDTO(
            email = user.email,
            name = user.name ?: "관리자", // 기본값 제공
            profileImg = user.profileImg // nullable 그대로 전달
        )
    }

    fun adminInfo(userId: Long): User {
        // 관리자 정보 조회 로직
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ResultCode.NOT_FOUND) }
        // 필요한 데이터만 반환 (현재 User 객체를 반환)
        return user
    }


}
