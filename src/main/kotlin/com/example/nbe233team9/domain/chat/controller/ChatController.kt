package com.example.nbe233team9.domain.chat.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.chat.dto.ChatMessageRequestDTO
import com.example.nbe233team9.domain.chat.dto.ChatMessageResponseDTO
import com.example.nbe233team9.domain.chat.service.ChatMessageService
import com.example.nbe233team9.domain.chat.service.RedisMessagePublisher
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@Tag(name = "chat", description = "채팅 API")
@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val redisMessagePublisher: RedisMessagePublisher,
    private val chatMessageService: ChatMessageService,
    private val messagingTemplate: SimpMessagingTemplate
) {

    private val log = LoggerFactory.getLogger(ChatController::class.java)

    /**
     * WebSocket을 통해 채팅 메시지 전송 및 Redis 발행
     */
    @MessageMapping("/chat/{roomId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    fun sendMessage(
        @DestinationVariable roomId: String,
        requestDTO: ChatMessageRequestDTO,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ) {
        try {
            val senderId = userDetails.getUserId()

            // 메시지 저장 및 발행
            val savedMessage = chatMessageService.sendMessage(senderId, requestDTO)

            // Redis 채널 발행
            redisMessagePublisher.publish("chatroom:$roomId", savedMessage)

            // WebSocket으로 메시지 브로드캐스트
            messagingTemplate.convertAndSend("/topic/chat/$roomId", savedMessage)

        } catch (e: Exception) {
            log.error("메시지 전송 중 오류 발생: {}", e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 전송 실패")
        }
    }

    /**
     * 채팅 로그 조회
     */
    @Operation(summary = "채팅방 메시지 로그 조회")
    @GetMapping("/rooms/{roomId}/messages")
    @PreAuthorize("hasRole('USER')")
    fun getMessagesByRoom(@PathVariable roomId: String): ApiResponse<List<ChatMessageResponseDTO>> {
        return ApiResponse.ok(chatMessageService.getMessagesByRoom(roomId))
    }
}