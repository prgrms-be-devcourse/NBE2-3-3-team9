package com.example.nbe233team9.domain.chat.handler

import com.example.nbe233team9.domain.auth.security.JwtTokenProvider
import com.example.nbe233team9.domain.chat.service.UserSessionService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class WebSocketChatHandler(
    private val userSessionService: UserSessionService,
    private val jwtTokenProvider: JwtTokenProvider
) : TextWebSocketHandler() {

    private val logger = LoggerFactory.getLogger(WebSocketChatHandler::class.java)

    /**
     * WebSocket 연결 성립 시 처리
     */
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val token = getTokenFromSession(session)

        if (!StringUtils.hasText(token) || !jwtTokenProvider.validateToken(token)) {
            logger.error("WebSocket 연결 실패: 유효하지 않은 JWT 토큰")
            session.close(CloseStatus.BAD_DATA)
            return
        }

        // 토큰에서 사용자 ID 추출
        val userId = jwtTokenProvider.getId(token).toString()

        // 사용자 연결 상태 저장
        userSessionService.setUserConnected(userId)

        // 사용자 ID를 세션에 저장
        session.attributes["userId"] = userId

        logger.info("WebSocket 연결 성공: 사용자 ID = {}", userId)
    }

    /**
     * WebSocket 연결 종료 시 처리
     */
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val userId = session.attributes["userId"] as? String

        if (!userId.isNullOrBlank()) {
            userSessionService.setUserDisconnected(userId)
            logger.info("WebSocket 연결 종료: 사용자 ID = {}, 상태 = {}", userId, status)
        }
    }

    /**
     * 메시지 수신 처리
     */
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val userId = session.attributes["userId"] as? String

        if (userId.isNullOrBlank()) {
            logger.error("메시지 수신 실패: 인증되지 않은 사용자")
            session.close(CloseStatus.NOT_ACCEPTABLE)
            return
        }

        val payload = message.payload
        logger.info("메시지 수신 - 사용자 ID: {}, 내용: {}", userId, payload)

        // Echo Message (테스트용)
        session.sendMessage(TextMessage("Echo: $payload"))
    }

    /**
     * 세션에서 JWT 토큰 추출
     */
    private fun getTokenFromSession(session: WebSocketSession): String {
        val attributes = session.attributes
        var token = attributes["Authorization"] as? String

        // 클라이언트에서 전송한 헤더에서 토큰 추출
        if (!StringUtils.hasText(token)) {
            val query = session.uri?.query  // 쿼리스트링에서 토큰 추출 (예: ?Authorization=token)
            if (!query.isNullOrBlank() && query.startsWith("Authorization=")) {
                token = query.removePrefix("Authorization=")
            }
        }

        return token ?: ""
    }
}