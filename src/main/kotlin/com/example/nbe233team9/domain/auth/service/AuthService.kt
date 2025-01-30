package com.example.nbe233team9.domain.auth.service

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.domain.auth.dto.TokenResponseDTO
import com.example.nbe233team9.domain.auth.security.JwtTokenProvider
import com.example.nbe233team9.domain.user.dto.LoginAdminDTO
import com.example.nbe233team9.domain.user.repository.UserRepository
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
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
            years_of_experience = user.years_of_experience,
            refreshToken = passwordEncoder.encode(refreshToken) // refreshToken 암호화 후 저장
        )

        userRepository.save(updatedUser)

        return mapOf(
            "accessToken" to accessToken,
            "userId" to user.id!!, // null이 아님을 보장
            "role" to user.role
        )

    }


    fun logout(userId: Long, response: HttpServletResponse): String {
        // 1. 사용자 조회
        val user = userRepository.findById(userId).orElseThrow {
            throw NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다.")
        }

        // 2. RefreshToken 초기화
        val updatedUser = user.copy(refreshToken = null) // 복사하여 refreshtoken만 변경

        userRepository.save(updatedUser)

        // 3. Cookie에서 RefreshToken 삭제
        val cookie = Cookie("REFRESHTOKEN", null).apply {
            domain = "localhost"
            path = "/"
            maxAge = 0 // 쿠키 만료 시간 0으로 설정
        }
        response.addCookie(cookie)

        // 4. 성공 결과 반환
        return "로그아웃 성공"
    }

    fun reCreateToken(request: HttpServletRequest): TokenResponseDTO {
        val cookies = request.cookies ?: throw CustomException(ResultCode.TOKEN_EXPIRED)

        var userId = 0L
        var refreshToken: String? = null

        // 쿠키에서 refreshToken과 userId 추출
        for (cookie in cookies) {
            if (cookie.name ==  "REFRESHTOKEN") {
                refreshToken = cookie.value
                userId = jwtTokenProvider.getId(refreshToken)
            }
        }

        if (refreshToken.isNullOrEmpty() || userId == 0L) {
            throw CustomException(ResultCode.TOKEN_EXPIRED)
        }

        // DB에서 사용자 조회 및 refreshToken 검증
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_USER) }

        if (!passwordEncoder.matches(refreshToken, user.refreshToken)) {
            throw CustomException(ResultCode.INVALID_TOKEN)
        }

        // accessToken 재발급
        val newAccessToken = jwtTokenProvider.createToken(userId)

        return TokenResponseDTO(newAccessToken)
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