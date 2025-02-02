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
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (accessor == null || accessor.command != StompCommand.CONNECT) {
            return message
        }

        val token = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER)
            ?.takeIf { it.startsWith(BEARER_PREFIX) }
            ?.removePrefix(BEARER_PREFIX)
            ?: throw IllegalArgumentException("Invalid or missing JWT Token").also {
                log.error("WebSocket 연결 실패: Authorization 헤더가 없거나 유효하지 않음")
            }

        if (!jwtTokenProvider.validateToken(token)) {
            log.error("WebSocket 연결 실패: 유효하지 않은 JWT 토큰")
            throw IllegalArgumentException("Invalid or expired JWT Token")
        }

        val userId = jwtTokenProvider.getId(token)
        val authentication = UsernamePasswordAuthenticationToken(userId, null, null)

        SecurityContextHolder.getContext().authentication = authentication
        accessor.user = authentication

        log.info("WebSocket 인증 성공: 사용자 ID = $userId")

        return message
    }
}