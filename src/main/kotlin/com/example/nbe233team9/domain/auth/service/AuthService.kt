package com.example.nbe233team9.domain.auth.service

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.domain.auth.security.JwtTokenProvider
import com.example.nbe233team9.domain.user.dto.LoginAdminDTO
import com.example.nbe233team9.domain.user.repository.UserRepository
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import lombok.AllArgsConstructor
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {


    fun login(loginAdminDTO: LoginAdminDTO, response: HttpServletResponse): Map<String, Any> {
        val optionalUser = userRepository.findByEmail(loginAdminDTO.email)
        if (optionalUser.isEmpty) {
            throw CustomException(ResultCode.NOT_EXISTS_USER)
        }
        val user = optionalUser.get()
        if (!passwordEncoder.matches(loginAdminDTO.password, user.password)) {
            throw CustomException(ResultCode.INVALID_USER_PASSWORD)
        }

        val accessToken = jwtTokenProvider.createToken(user.id!!)
        val refreshToken = jwtTokenProvider.createRefreshToken(user.id!!)

        val refreshTokenCookie = createRefreshTokenCookie(refreshToken, "localhost")
        response.addCookie(refreshTokenCookie)

        // 업데이트된 User 객체 생성
        val updatedUser = user.copy(
            name = user.name,
            profileImg = user.profileImg,
            email = user.email,
            password = user.password,
            role = user.role,
            socialAccessToken = user.socialAccessToken,
            yearsOfExperience = user.yearsOfExperience,
            refreshToken = passwordEncoder.encode(refreshToken) // refreshToken 암호화 후 저장
        )

        userRepository.save(updatedUser)

        return mapOf(
            "accessToken" to accessToken,
            "userId" to user.id!!, // null이 아님을 보장
            "role" to user.role
        )

    }



    fun createRefreshTokenCookie(refreshToken: String, domain: String): Cookie {
        return Cookie("REFRESHTOKEN", refreshToken).apply {
            isHttpOnly = true // 보안 강화
            path = "/" // 모든 경로에서 쿠키 사용 가능
            this.domain = domain // 도메인 설정
            maxAge = 604800 // 7일 (초 단위)
        }
    }


}