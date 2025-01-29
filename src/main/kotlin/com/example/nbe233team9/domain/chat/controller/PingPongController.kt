package com.example.nbe233team9.domain.chat.controller

import com.example.nbe233team9.domain.chat.service.UserSessionService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class PingPongController(
    private val userSessionService: UserSessionService
) {

    // 클라이언트에서 /app/ping으로 메시지를 보내면 처리
    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    fun handlePing(accessor: StompHeaderAccessor): String {
        // 사용자 ID를 가져와서 TTL 갱신
        accessor.user?.name?.let { userId ->
            userSessionService.refreshUserStatus(userId)  // TTL 갱신
        }

        // 서버가 Pong 응답을 전송
        return "pong"
    }
}