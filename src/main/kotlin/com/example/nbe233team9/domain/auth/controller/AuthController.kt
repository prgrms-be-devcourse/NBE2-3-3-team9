package com.example.nbe233team9.domain.auth.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.dto.TokenResponseDTO
import com.example.nbe233team9.domain.auth.security.JwtTokenProvider
import com.example.nbe233team9.domain.auth.service.AuthService
import com.example.nbe233team9.domain.auth.service.KakaoService
import com.example.nbe233team9.domain.user.dto.LoginAdminDTO
import com.example.nbe233team9.domain.user.dto.UserResponseDTO
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val kakaoService: KakaoService
) {

    @Value("\${kakao.client-id}")
    private lateinit var clientId: String

    @Value("\${kakao.redirect-uri}")
    private lateinit var redirectUri: String



    @Operation(summary = "관리자 로그인")
    @PostMapping("/login")
    fun login(@Valid @RequestBody loginAdminDTO: LoginAdminDTO, response: HttpServletResponse) : ApiResponse<Map<String, Any>> {
        val admin = authService.login(loginAdminDTO, response)
        return ApiResponse.ok(admin);
    }

    @Operation(summary = "카카오 로그인")
    @GetMapping("/kakao")
    fun redirectToKakao(response: HttpServletResponse) {
        val kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=$clientId" +
                "&redirect_uri=$redirectUri" +
                "&scope=profile_nickname,account_email"

        response.sendRedirect(kakaoLoginUrl)
    }



    @Operation(summary = "카카오 로그인 콜백")
    @GetMapping("/kakao/callback")
    fun kakaoCallback(@RequestParam code: String, response: HttpServletResponse): ApiResponse<Map<String, Any>> {
        // 1. Kakao AccessToken 가져오기
        val kakaoAccessToken = kakaoService.getAccessToken(code)

        // 2. 사용자 검증 및 JWT AccessToken 생성
        val userInfo: UserResponseDTO = kakaoService.validateUser(kakaoAccessToken)

        // 3. RefreshToken 생성 및 쿠키에 저장
        val refreshToken = jwtTokenProvider.createRefreshToken(userInfo.id)
        val refreshTokenCookie: Cookie = authService.createRefreshTokenCookie(refreshToken, "localhost")
        response.addCookie(refreshTokenCookie)

        // 4. AccessToken 반환
        val tokenResponseDTO = TokenResponseDTO(userInfo.accessToken)

        // 5. userId와 함께 응답 반환
        val responseMap = mapOf(
            "accessToken" to tokenResponseDTO.accessToken,
            "userId" to userInfo.id,
            "role" to userInfo.role
        )

        return ApiResponse.ok(responseMap)
    }
}