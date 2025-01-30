package com.example.nbe233team9.domain.auth.service

import com.example.nbe233team9.domain.auth.client.KakaoClient
import com.example.nbe233team9.domain.auth.security.JwtTokenProvider
import com.example.nbe233team9.domain.user.dto.UserResponseDTO
import com.example.nbe233team9.domain.user.model.Role
import com.example.nbe233team9.domain.user.service.UserService

import com.fasterxml.jackson.databind.JsonNode
import lombok.RequiredArgsConstructor

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class KakaoService(
    private val kakaoClient: KakaoClient,
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder
) {

    @Value("\${KAKAO_CLIENT_ID}")
    private lateinit var kakaoClientId: String

    @Value("\${KAKAO_REDIRECT_URI}")
    private lateinit var kakaoRedirectUri: String

    fun getAccessToken(code: String): String {
        return kakaoClient.getAccessToken(kakaoClientId, kakaoRedirectUri, code)
    }

    fun validateUser(kakaoAccessToken: String): UserResponseDTO {
        val userInfo: JsonNode = kakaoClient.getUserInfo(kakaoAccessToken)

        val kakaoAccountNode = userInfo.path("kakao_account")
        val profileNode = kakaoAccountNode.path("profile")

        val nickname = profileNode.path("nickname").asText("Unknown")
        val email = kakaoAccountNode.path("email").asText("Unknown")
        val profileImg = profileNode.path("profile_image_url").asText("")


        // 사용자 저장 또는 기존 사용자 반환
        val user = userService.saveUser(nickname, email, profileImg, Role.USER)
        val userId = requireNotNull(user.id) { "User ID cannot be null" }
        // JWT AccessToken 및 RefreshToken 생성
        val jwtAccessToken = jwtTokenProvider.createToken(userId)
        val refreshToken = jwtTokenProvider.createRefreshToken(userId)

        // RefreshToken 암호화 후 저장
        val updatedUser = user.copy(
            refreshToken = passwordEncoder.encode(refreshToken),
            socialAccessToken = kakaoAccessToken
        )
        userService.updateUser(updatedUser)

        return UserResponseDTO(
            id = user.id ?: 0L,
            nickname = nickname,
            email = email,
            accessToken = jwtAccessToken,
            refreshToken = refreshToken,
            profileImg = profileImg,
            role = user.role
        )

    }
}
