package com.example.nbe233team9.domain.chat.service

import com.example.nbe233team9.domain.chat.dto.ChatMessageResponseDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate

class RedisMessageSubscriber (
    private val objectMapper: ObjectMapper, // JSON 역직렬화
    private val messagingTemplate: SimpMessagingTemplate // WebSocket 메시지 전송
){
    private val log = LoggerFactory.getLogger(RedisMessageSubscriber::class.java)

    companion object {
        private const val MAX_WEBSOCKET_RETRIES = 3 // WebSocket 전송 최대 재시도 횟수
    }

    /**
     * Redis로부터 수신한 메시지 처리
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
     * WebSocket으로 메시지 전송 (재시도 포함)
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