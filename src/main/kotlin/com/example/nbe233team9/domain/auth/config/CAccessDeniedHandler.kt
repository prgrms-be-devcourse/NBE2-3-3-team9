package com.example.nbe233team9.domain.auth.config

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.common.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import com.fasterxml.jackson.databind.ObjectMapper

@Component
class CAccessDeniedHandler : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        // 기본 응답 값
        var resultCode = ResultCode.FORBIDDEN

        // SecurityContext에서 권한(Role) 확인
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication != null && authentication.authorities != null) {
            val isAdmin = authentication.authorities.any { it.authority == "ROLE_ADMIN" }

            resultCode = if (!isAdmin) {
                ResultCode.ACCESS_DENIED_ADMIN // 관리자 권한 부족
            } else {
                ResultCode.ACCESS_DENIED_USER // 사용자 권한 부족
            }
        }

        // ApiResponse 생성
        val errorResponse = ApiResponse.fail<Any>(
            CustomException(resultCode) // CustomException 생성자로 ResultCode 전달
        )

        // JSON 변환 및 응답
        val objectMapper = ObjectMapper()
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
