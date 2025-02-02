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
            ?: return message

        if (accessor.command == StompCommand.CONNECT) {
            val token = accessor.getFirstNativeHeader("Authorization")?.removePrefix("Bearer ")
            if (!token.isNullOrBlank() && jwtTokenProvider.validateToken(token)) {
                val userId = jwtTokenProvider.getId(token)
                val authentication = UsernamePasswordAuthenticationToken(userId, null, null)

                // ✅ SecurityContext 설정
                val securityContext = SecurityContextHolder.createEmptyContext()
                securityContext.authentication = authentication
                SecurityContextHolder.setContext(securityContext)

                accessor.user = authentication // WebSocket 세션에 사용자 설정

                log.info("✅ WebSocket 인증 성공: 사용자 ID = {}", userId)
            }
        }
        return message
    }
}