package com.example.nbe233team9.domain.user.service

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.domain.user.dto.AdminResponseDTO
import com.example.nbe233team9.domain.user.dto.CreateAdminDTO
import com.example.nbe233team9.domain.user.dto.UpdateAdminDTO
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

    fun adminUpdate(id: Long, updateAdminDTO: UpdateAdminDTO): String {
        val user = userRepository.findById(id).orElseThrow { RuntimeException("사용자를 찾을 수 없습니다.") }

        val updatedUser = user.copy(
            name = updateAdminDTO.name ?: user.name,
            password = updateAdminDTO.password ?: user.password,
            profileImg = updateAdminDTO.profileImg ?: user.profileImg,
            email = updateAdminDTO.email ?: user.email,
//            pets = user.pets,
//            refreshtoken = user.refreshtoken,
//            chatMessages = user.chatMessages,
//            chatRooms = user.chatRooms,
            socialAccessToken = user.socialAccessToken,
            years_of_experience =user.years_of_experience,
            role = user.role,
//            communities = user.communities
        )

        userRepository.save(updatedUser)
        return "업데이트 성공"
    }



}
