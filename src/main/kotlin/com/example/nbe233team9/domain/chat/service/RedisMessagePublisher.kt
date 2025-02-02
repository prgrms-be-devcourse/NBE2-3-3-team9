package com.example.nbe233team9.domain.chat.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.example.nbe233team9.domain.chat.dto.ChatMessageRequestDTO
import com.example.nbe233team9.domain.chat.dto.ChatMessageResponseDTO
import com.example.nbe233team9.domain.chat.entity.ChatMessage
import com.example.nbe233team9.domain.chat.entity.ChatRoom
import com.example.nbe233team9.domain.chat.repository.ChatMessageRepository
import com.example.nbe233team9.domain.chat.repository.ChatRoomRepository
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.repository.UserRepository
import com.example.nbe233team9.global.constants.ChatConstants.SYSTEM_USER_EMAIL
import com.example.nbe233team9.global.constants.ChatConstants.SYSTEM_USER_ID
import com.example.nbe233team9.global.constants.ChatConstants.SYSTEM_USER_NAME
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class RedisMessagePublisher(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val userRepository: UserRepository
) {

    private val log = LoggerFactory.getLogger(RedisMessagePublisher::class.java)

    companion object {
        private const val MAX_RETRIES = 3 // 최대 재시도 횟수
        private const val INITIAL_RETRY_DELAY_MS = 1000L // 초기 대기 시간 1초
    }

    /**
     * Redis 채널로 메시지를 발행하고 DB에 저장
     */
    fun publish(channel: String, responseDTO: ChatMessageResponseDTO) {
        var delay = INITIAL_RETRY_DELAY_MS

        repeat(MAX_RETRIES) { attempt ->
            runCatching {
                val messageJson = objectMapper.writeValueAsString(responseDTO)
                redisTemplate.convertAndSend(channel, messageJson)
                log.info("Redis 채널에 메시지 발행 완료: channel={}, message={}", channel, messageJson)
                return
            }.onFailure { e ->
                log.error("Redis 메시지 발행 실패 (시도 ${attempt + 1}): ${e.message}")

                if (attempt == MAX_RETRIES - 1) {
                    log.warn("Redis 발행 실패. DB에만 저장됨.")
                    return
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(delay)
                    delay *= 2 // 대기 시간 두 배 증가 (백오프)
                } catch (interruptedException: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw RuntimeException("재시도 대기 중 인터럽트 발생", interruptedException)
                }
            }
        }
    }

    /**
     * 메시지를 DB에 저장
     */
    private fun saveChatMessage(senderId: Long?, requestDTO: ChatMessageRequestDTO): ChatMessage {
        val sender = senderId?.let { findUserById(it) } ?: getSystemUser()
        val chatRoom = findChatRoomById(requestDTO.roomId)

        val chatMessage = ChatMessage(
            sender = sender,
            content = requestDTO.content,
            type = requestDTO.type,
            chatRoom = chatRoom,
            sentAt = LocalDateTime.now()
        )

        chatMessageRepository.save(chatMessage)

        // 마지막 메시지 업데이트
        updateLastMessage(chatRoom, chatMessage)

        return chatMessage
    }

    /**
     * 채팅방의 마지막 메시지 업데이트
     */
    private fun updateLastMessage(chatRoom: ChatRoom, chatMessage: ChatMessage) {
        chatRoom.apply {
            lastMessage = chatMessage.content
            lastMessageTime = chatMessage.sentAt
        }
        chatRoomRepository.save(chatRoom)
    }

    /**
     * ChatMessage → ChatMessageResponseDTO 변환
     */
    private fun convertToResponseDTO(chatMessage: ChatMessage): ChatMessageResponseDTO =
        ChatMessageResponseDTO(
            messageId = chatMessage.id!!,
            roomId = chatMessage.chatRoom.roomId,
            senderName = chatMessage.sender.name!!,
            senderId = chatMessage.sender.id!!,
            content = chatMessage.content,
            type = chatMessage.type,
            sentAt = chatMessage.sentAt
        )

    /**
     * 사용자 조회
     */
    private fun findUserById(userId: Long): User =
        userRepository.findById(userId).orElseThrow { EntityNotFoundException("사용자를 찾을 수 없습니다. ID: $userId") }

    /**
     * 채팅방 조회
     */
    private fun findChatRoomById(roomId: String): ChatRoom =
        chatRoomRepository.findByRoomId(roomId).orElseThrow { EntityNotFoundException("채팅방을 찾을 수 없습니다. RoomID: $roomId") }

    /**
     * 시스템 유저 생성
     */
    private fun getSystemUser(): User =
        User(id = SYSTEM_USER_ID, name = SYSTEM_USER_NAME, email = SYSTEM_USER_EMAIL)
}
