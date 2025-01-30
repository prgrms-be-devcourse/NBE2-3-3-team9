package com.example.nbe233team9.domain.user.service

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.domain.auth.client.KakaoClient
import com.example.nbe233team9.domain.user.dto.UpdateUserDTO
import com.example.nbe233team9.domain.user.dto.UserDetailResponseDTO
import com.example.nbe233team9.domain.user.model.Role
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.NoSuchElementException


@Service
class UserService(
    private val userRepository: UserRepository,
    private val kakaoClient: KakaoClient
) {

    fun getUserInfo(userId: Long) : UserDetailResponseDTO {
        val user = userRepository.findById(userId)
            .orElseThrow {CustomException(ResultCode.NOT_EXISTS_USER)}
        return UserDetailResponseDTO(
            id = user.id!!,
            name = user.name!!,
            email = user.email,
            profileImg = user.profileImg,
            years_of_experience = user.years_of_experience,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }

    fun userUpdate(id: Long, updateUserDTO: UpdateUserDTO) : String {
        val user =userRepository.findById(id)
            .orElseThrow {CustomException(ResultCode.NOT_EXISTS_USER)}

        val updateUser =user.copy(
            name = updateUserDTO.name.takeIf { it.isNotEmpty() } ?: user.name, // 이름이 비어있지 않으면 업데이트, 아니면 기존 값 유지
            years_of_experience = updateUserDTO.years_of_experience.takeIf { it != 0 } ?: user.years_of_experience // 0이 아니면 업데이트, 아니면 기존 값 유지
        )

        userRepository.save(updateUser)
        return "업데이트 성공"
    }


    fun deleteUser(id: Long): String {
        val user = userRepository.findById(id).orElseThrow {
            throw NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다.")
        }
        if(user.role == Role.USER) {val kakaoAccessToken = user.socialAccessToken // 저장된 Kakao Access Token

            if (!kakaoAccessToken.isNullOrEmpty()) {
                kakaoClient.unlinkKakaoAccount(kakaoAccessToken) // Kakao 계정 연결 해제
            }
        }

        userRepository.deleteById(user.id!!)
        return "삭제 성공"
    }


    fun saveUser(nickname: String, email: String, profileImg: String, role: Role): User {
        val existingUser = userRepository.findByEmail(email)
        return existingUser.orElseGet {
            val user = User(
                name = nickname,
                email = email,
                profileImg = profileImg,
                role = role
            )
            userRepository.save(user)
        }
    }

    fun updateUser(user: User) {
        userRepository.save(user)
    }



}