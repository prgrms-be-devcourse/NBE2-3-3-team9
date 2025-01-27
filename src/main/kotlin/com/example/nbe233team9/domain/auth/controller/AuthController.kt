package com.example.nbe233team9.domain.auth.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.security.JwtTokenProvider
import com.example.nbe233team9.domain.auth.service.AuthService
import com.example.nbe233team9.domain.user.dto.LoginAdminDTO
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenProvider: JwtTokenProvider
) {


    @Operation(summary = "관리자 로그인")
    @PostMapping("/login")
    fun login(@Valid @RequestBody loginAdminDTO: LoginAdminDTO, response: HttpServletResponse) : ApiResponse<Map<String, Any>> {
        val admin = authService.login(loginAdminDTO, response)
        return ApiResponse.ok(admin);
    }
}