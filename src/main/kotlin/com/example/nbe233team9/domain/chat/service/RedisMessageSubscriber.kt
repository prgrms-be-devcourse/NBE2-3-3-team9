package com.example.nbe233team9.domain.chat.service

import com.example.nbe233team9.domain.chat.dto.ChatMessageResponseDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

/**
 * Redis에서 메시지를 구독하고,
 * 수신된 메시지를 JSON으로 역직렬화한 후,
 * WebSocket을 통해 클라이언트로 전달함
 * (redis와 WebSocket 사이에서 메시지 브로커 역할을 함)
 */
@Component
class RedisMessageSubscriber (
    private val objectMapper: ObjectMapper, // JSON 역직렬화
    private val messagingTemplate: SimpMessagingTemplate // WebSocket 메시지 전송
){
    private val log = LoggerFactory.getLogger(RedisMessageSubscriber::class.java)

    companion object {
        private const val MAX_WEBSOCKET_RETRIES = 3 // WebSocket 전송 최대 재시도 횟수
    }

    /**
     * Redis로부터 메시지가 들어오면 호출됨
     * 메시지를 역직렬화해서 sendToWebSocket으로 전달함
     */
    fun handleMessage(message: String) {
        try {
            val chatMessage = objectMapper.readValue(message, ChatMessageResponseDTO::class.java)
            sendToWebSocket(chatMessage)
        } catch (e: Exception) {
            log.error("Redis 메시지 처리 중 오류 발생: {}", e.message)
        }
    }

    /**
     * WebSocket을 통해 특정 topic에 전송하여
     * 해당 경로를 구독 중인 클라이언트가 해당 메시지를 수신할 수 있도록 함
     */
    private fun sendToWebSocket(chatMessage: ChatMessageResponseDTO) {
        var attempt = 0

        while (attempt < MAX_WEBSOCKET_RETRIES) {
            try {
                messagingTemplate.convertAndSend("/topic/chat/room/${chatMessage.roomId}", chatMessage)
                log.info("WebSocket으로 메시지 전송 성공: RoomID={}, Content={}", chatMessage.roomId, chatMessage.content)
                break // 성공 시 루프 종료
            } catch (e: Exception) {
                attempt++
                log.warn("WebSocket 전송 실패 (시도 {}): {}", attempt, e.message)

                if (attempt >= MAX_WEBSOCKET_RETRIES) {
                    log.error("WebSocket 전송 최종 실패. 메시지 전송 포기: {}", chatMessage.content)
                }
            }
        }
    }
}