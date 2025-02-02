package com.example.nbe233team9.domain.chat.config

import com.example.nbe233team9.domain.chat.service.UserSessionService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.security.Principal

@Component
class WebSocketEventListener(
    private val userSessionService: UserSessionService
) {
    private val log = LoggerFactory.getLogger(WebSocketEventListener::class.java)

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val userPrincipal: Principal? = headerAccessor.user

        userPrincipal?.let {
            val userId = it.name
            log.info("사용자 연결됨: $userId")
            userSessionService.setUserConnected(userId)
        }
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val userPrincipal: Principal? = headerAccessor.user

        userPrincipal?.let {
            val userId = it.name
            log.info("사용자 연결 해제됨: $userId")
            userSessionService.setUserDisconnected(userId)
        }
    }
}
