package com.example.nbe233team9.domain.auth.security


import com.example.nbe233team9.domain.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    companion object {
        private val SWAGGER_WHITELIST = listOf(
            "/swagger-ui.html",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/api-docs/",
            "/api-ui.html"
        )
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestURI = request.requestURI

        // Swagger 및 WebSocket 경로는 필터링하지 않음
        if (SWAGGER_WHITELIST.any { requestURI.startsWith(it) }        ) {
            filterChain.doFilter(request, response)
            return
        }

        val token = getTokenFromHeader(request)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            val userId = jwtTokenProvider.getId(token)
            val optionalUser = userRepository.findById(userId)

            if (optionalUser.isPresent) {
                val user = optionalUser.get()

                // CustomUserDetails 객체 생성
                val userDetails = CustomUserDetails(user, userId)

                // Spring Security 인증 객체 생성 및 설정
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )

                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun getTokenFromHeader(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (!bearerToken.isNullOrEmpty() && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val excludePath = listOf(
            "/swagger-ui/index.html",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/swagger-ui/swagger-initializer.js",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui/swagger-ui.css",
            "/swagger-ui/index.css",
            "/swagger-ui/favicon-32x32.png",
            "/swagger-ui/favicon-16x16.png",
            "/api-docs/json/swagger-config",
            "/api-docs/json"
        )
        val path = request.requestURI
        return excludePath.any { path.startsWith(it) }
    }
}
