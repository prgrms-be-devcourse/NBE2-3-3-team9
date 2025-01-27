package com.example.nbe233team9.domain.chat.config

import com.example.nbe233team9.domain.auth.security.JwtTokenProvider
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import java.security.Principal

@Component
@Slf4j
class StompChannelInterceptor (
    private val jwtTokenProvider: JwtTokenProvider
) : ChannelInterceptor {

    // 로거 객체 초기화
    private val log = LoggerFactory.getLogger(StompChannelInterceptor::class.java)

    // WebSocket 메시지가 전송되기 전에 실행됨
    // JWT 토큰을 검증하고, 사용자 정보를 WebSocket 세션에 설정해줌
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {

        // StompHeaderAccessor 객체 생성 <- 메시지의 헤더 정보를 읽고 조작할 수 있도록
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java) ?: return message

        // WebSocket 요청의 명령어가 CONNECT면, JWT 검증을 수행함
        val command = accessor.command
        if(command == StompCommand.CONNECT) {

            // 헤더에서 JWT 토큰을 추출함
            var token = accessor.getFirstNativeHeader("Authorization")
            if(!token.isNullOrBlank() && token.startsWith("Bearer ")) {
                token = token.substring("Bearer ".length)
            }

            // JWT 토큰의 유효성을 검증함
            if(token.isNullOrBlank() || !jwtTokenProvider.validateToken(token)) {
                log.error("WebSocket 연결 실패: 유효하지 않은 JWT 토큰")
                throw IllegalArgumentException("Invalid or missing JWT Token")
            }

            // 사용자 ID를 추출해서 WebSocket 세션에 저장함
            val userId = jwtTokenProvider.getId(token).toString()
            accessor.user = Principal { userId }

            log.info("WebSocket 인증 성공: 사용자 ID = $userId")
        }

        return message
    }
}