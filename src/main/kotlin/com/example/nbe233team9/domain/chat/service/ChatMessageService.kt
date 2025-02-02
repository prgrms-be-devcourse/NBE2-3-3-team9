package com.example.nbe233team9.domain.chat.service

import com.example.nbe233team9.domain.chat.dto.ChatMessageRequestDTO
import com.example.nbe233team9.domain.chat.dto.ChatMessageResponseDTO
import com.example.nbe233team9.domain.chat.entity.ChatMessage
import com.example.nbe233team9.domain.chat.entity.ChatRoom
import com.example.nbe233team9.domain.chat.repository.ChatMessageRepository
import com.example.nbe233team9.domain.chat.repository.ChatParticipantRepository
import com.example.nbe233team9.domain.chat.repository.ChatRoomRepository
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.repository.UserRepository
import com.example.nbe233team9.global.constants.ChatConstants
import com.example.nbe233team9.global.constants.ChatConstants.SYSTEM_USER_NAME
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * ChatMessageService
 * - 채팅 메시지 송수신, 조회, 시스템 메시지 관리 등 메시지 관련 비즈니스 로직 처리
 */
@Service
@Transactional
class ChatMessageService(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val userRepository: UserRepository,
    private val chatServiceUtil: ChatServiceUtil
) {

    /**
     * 일반 메시지 전송
     */
    fun sendMessage(senderId: Long, requestDTO: ChatMessageRequestDTO): ChatMessageResponseDTO {
        validateMessageContent(requestDTO.content)
        return processAndSendMessage(senderId, requestDTO)
    }

    /**
     * 시스템 메시지 전송
     */
    fun sendSystemMessage(content: String, roomId: String, type: ChatMessage.MessageType) {
        val requestDTO = ChatMessageRequestDTO(
            content = content,
            type = type,
            roomId = roomId
        )
        processAndSendMessage(null, requestDTO)
    }

    /**
     * 공통 메시지 처리 로직
     */
    private fun processAndSendMessage(senderId: Long?, requestDTO: ChatMessageRequestDTO): ChatMessageResponseDTO {
        val sender = senderId?.let { chatServiceUtil.findUserById(it) } ?: chatServiceUtil.getSystemUser()
        val chatRoom = chatServiceUtil.findChatRoomById(requestDTO.roomId)

        if (senderId != null) {
            chatServiceUtil.validateParticipant(sender, chatRoom)
        }

        // ✅ 상대방(Receiver) 조회 (발신자가 아닌 사람)
        val receiver = chatParticipantRepository.findByChatRoom(chatRoom)
            .map { it.user }
            .firstOrNull { it.id != sender.id }

        val chatMessage = createChatMessage(sender, receiver, requestDTO, chatRoom)
        saveMessageAndUpdateRoom(chatMessage, chatRoom)

        return convertToResponseDTO(chatMessage)
    }

    /**
     * 메시지 내용 유효성 검사
     */
    private fun validateMessageContent(content: String?) {
        require(!content.isNullOrBlank()) { "메시지 내용은 비어 있을 수 없습니다." }
        require(content.length <= 1000) { "메시지 내용은 1000자를 초과할 수 없습니다." }
    }

    /**
     * 특정 채팅방의 메시지 목록 조회
     */
    fun getMessagesByRoom(roomId: String): List<ChatMessageResponseDTO> {
        val chatRoom = chatServiceUtil.findChatRoomById(roomId)
        return chatMessageRepository.findByChatRoomOrderBySentAtAsc(chatRoom)
            .map { convertToResponseDTO(it) }
    }

    /**
     * 메시지 생성 (일반/시스템 공통)
     */
    private fun createChatMessage(
        sender: User,
        receiver: User?,
        requestDTO: ChatMessageRequestDTO,
        chatRoom: ChatRoom
    ): ChatMessage {
        return ChatMessage(
            sender = sender,
            receiver = receiver,
            content = requestDTO.content,
            type = requestDTO.type,
            chatRoom = chatRoom,
            sentAt = LocalDateTime.now()
        )
    }

    /**
     * 메시지 저장 및 채팅방의 마지막 메시지 업데이트
     */
    private fun saveMessageAndUpdateRoom(chatMessage: ChatMessage, chatRoom: ChatRoom) {
        chatMessageRepository.save(chatMessage)
        chatRoom.apply {
            lastMessage = chatMessage.content
            lastMessageTime = chatMessage.sentAt
        }
        chatRoomRepository.save(chatRoom)
    }

    /**
     * ChatMessage → ChatMessageResponseDTO 변환
     */
    private fun convertToResponseDTO(chatMessage: ChatMessage): ChatMessageResponseDTO {
        val chatRoom = chatMessage.chatRoom
        val sender = chatMessage.sender

        val opponentParticipant = chatParticipantRepository.findByChatRoom(chatRoom)
            .firstOrNull { it.user.id != sender!!.id }

        val opponent = opponentParticipant?.user

        return ChatMessageResponseDTO(
            messageId = chatMessage.id!!,
            roomId = chatMessage.chatRoom.roomId,
            senderName = sender!!.name ?: SYSTEM_USER_NAME,
            senderId = sender.id ?: userRepository.findByEmail(ChatConstants.SYSTEM_USER_EMAIL)
                .orElseThrow { EntityNotFoundException("시스템 유저를 찾을 수 없습니다.") }
                .id!!,
            content = chatMessage.content,
            type = chatMessage.type,
            sentAt = chatMessage.sentAt,

            // 상대방 정보 추가
            opponentId = opponent?.id,
            opponentName = opponent?.name ?: "상대방 없음",
            opponentProfileImg = opponent?.profileImg
        )
    }
}