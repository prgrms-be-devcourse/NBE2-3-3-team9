package com.example.nbe233team9.domain.auth.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.dto.TokenResponseDTO
import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.auth.security.JwtTokenProvider
import com.example.nbe233team9.domain.auth.service.AuthService
import com.example.nbe233team9.domain.auth.service.KakaoService
import com.example.nbe233team9.domain.schedule.service.RedisService
import com.example.nbe233team9.domain.user.dto.LoginAdminDTO
import com.example.nbe233team9.domain.user.dto.UserResponseDTO
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.Duration


@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val kakaoService: KakaoService,
    private val redisService: RedisService
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

    @Operation(summary = "로그아웃")
    @GetMapping("/logout")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    fun logout(@AuthenticationPrincipal userDetails: CustomUserDetails, response: HttpServletResponse) : ApiResponse<String> {
        val logout = authService.logout(userDetails.getUserId(), response);
        return ApiResponse.ok(logout);
    }

    @PostMapping("/new-token")
    @Operation(summary = "토큰 재발급", description = "access토큰 재발급")
    fun reCreate(request: HttpServletRequest) :ApiResponse<TokenResponseDTO> {
        val reCreate = authService.reCreateToken(request)
        return ApiResponse.ok(reCreate);
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

        // 6. Redis에 AccessToken 저장
        val redisKey = java.lang.String.format("user.%s.access_token", userInfo.id)
        redisService.setValues(redisKey, kakaoAccessToken, Duration.ofMinutes((5 * 60 + 50).toLong()))

        return ApiResponse.ok(responseMap)
    }
}