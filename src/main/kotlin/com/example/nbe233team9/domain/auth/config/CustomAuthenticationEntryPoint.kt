package com.example.nbe233team9.domain.auth.config

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.common.response.ApiResponse

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        // ApiResponse 객체 생성
        val errorResponse = ApiResponse.fail<Any>(
            CustomException(ResultCode.NOT_FOUND) // CustomException으로 ResultCode 전달
        )

        // JSON 형식으로 변환 및 응답
        val objectMapper = ObjectMapper()
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
        response.writer.flush()
    }
}
