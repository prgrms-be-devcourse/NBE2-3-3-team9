package com.example.nbe233team9.domain.chat.config

import com.example.nbe233team9.domain.auth.security.JwtTokenProvider
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.security.Principal

@Component
@Slf4j
class StompChannelInterceptor (
    private val jwtTokenProvider: JwtTokenProvider
) : ChannelInterceptor {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private val log = LoggerFactory.getLogger(StompChannelInterceptor::class.java)
    }

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        // 메시지에서 StompHeaderAccessor를 가져옴
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        // accessor가 null이거나 CONNECT 명령이 아니면 메시지를 그대로 반환
        if (accessor == null || accessor.command != StompCommand.CONNECT) {
            return message
        }

        // Authorization 헤더에서 JWT 토큰 추출
        val token = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER)
            ?.takeIf { it.startsWith(BEARER_PREFIX) }
            ?.substring(BEARER_PREFIX.length)

        if (token.isNullOrBlank()) {
            log.error("❌ WebSocket 연결 실패: Authorization 헤더가 없거나 유효하지 않음")
            throw IllegalArgumentException("Invalid or missing JWT Token")
        }

        // JWT 토큰 검증
        if (!jwtTokenProvider.validateToken(token)) {
            log.error("❌ WebSocket 연결 실패: 유효하지 않은 JWT 토큰")
            throw IllegalArgumentException("Invalid or expired JWT Token")
        }

        // JWT 토큰에서 사용자 ID 추출
        val userId = jwtTokenProvider.getId(token)

        // Spring Security의 Authentication 객체 생성
        val authentication = UsernamePasswordAuthenticationToken(userId, null, emptyList())

        // SecurityContext 설정 (WebSocket 인증을 위한 SecurityContext)
        SecurityContextHolder.getContext().authentication = authentication

        // WebSocket 세션에 사용자 정보 설정 (WebSocket Principal에 설정)
        accessor.user = authentication

        log.info("✅ WebSocket 인증 성공: 사용자 ID = {}", userId)

        return message
    }
}