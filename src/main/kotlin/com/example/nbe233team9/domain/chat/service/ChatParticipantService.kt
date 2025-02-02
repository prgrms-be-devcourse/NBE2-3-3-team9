package com.example.nbe233team9.domain.chat.service

import com.example.nbe233team9.domain.chat.entity.ChatMessage
import com.example.nbe233team9.domain.chat.entity.ChatParticipant
import com.example.nbe233team9.domain.chat.repository.ChatMessageRepository
import com.example.nbe233team9.domain.chat.repository.ChatParticipantRepository
import com.example.nbe233team9.domain.chat.repository.ChatRoomRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Transactional
class ChatParticipantService(
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatServiceUtil: ChatServiceUtil,
    private val chatMessageService: ChatMessageService
) {

    /**
     * 채팅방 입장 처리
     * - 참여자 정보를 생성하고 저장
     *
     * @param roomId 채팅방 ID
     * @param userId 참여자 ID
     * @param isAdmin 관리자 여부
     */
    fun joinChatRoom(roomId: String, userId: Long, isAdmin: Boolean) {
        val chatRoom = chatServiceUtil.findChatRoomById(roomId)
        val user = chatServiceUtil.findUserById(userId)

        // 이미 참여 중인지 확인
        val participant = chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)

        if (participant.isPresent) {
            if (!participant.get().isActive) {
                participant.get().isActive = true
                chatParticipantRepository.save(participant.get())
            }
        } else {
            // 새 참여자 등록
            val newParticipant = ChatParticipant(
                chatRoom = chatRoom,
                user = user,
                isAdmin = isAdmin,
                isActive = true,
                joinedAt = LocalDateTime.now()
            )
            chatParticipantRepository.save(newParticipant)
        }

        // 관리자가 참여하면 occupied 상태를 true로 변경
        if (isAdmin) {
            chatRoom.occupied = true
            chatRoomRepository.save(chatRoom)
        }

        // ✅ 관리자가 입장했을 때 시스템 메시지 전송 (없는 게 나은가?)
        if (isAdmin) {
            chatMessageService.sendSystemMessage("관리자가 입장했습니다. 궁금한 점을 물어보세요!", roomId, ChatMessage.MessageType.SYSTEM)
        }
    }

    /**
     * 채팅방 퇴장 처리
     * - 사용자가 채팅방에서 나가면 isActive를 false로 변경
     *
     * @param roomId 채팅방 ID
     * @param userId 퇴장할 사용자 ID
     */
    /**
     * 채팅방 퇴장 처리
     * - 사용자가 채팅방에서 나가면 isActive를 false로 변경
     *
     * @param roomId 채팅방 ID
     * @param userId 퇴장할 사용자 ID
     */
    fun leaveChatRoom(roomId: String, userId: Long, isAdmin: Boolean) {

        val chatRoom = chatServiceUtil.findChatRoomById(roomId)
        val user = chatServiceUtil.findUserById(userId)

        // 참여자 비활성화 처리
        val participant = chatServiceUtil.findParticipantByUserAndRoom(user, chatRoom)
        participant.isActive = false
        chatParticipantRepository.save(participant)

        // 시스템 메시지 전송
        val content = if (isAdmin) "관리자가 퇴장했습니다." else "사용자가 퇴장했습니다."
        chatMessageService.sendSystemMessage(content, roomId, ChatMessage.MessageType.SYSTEM)

        // 사용자가 나가면 무조건 삭제
        if (!isAdmin) {
            chatMessageRepository.deleteByChatRoom(chatRoom) // 채팅 메시지 삭제
            chatParticipantRepository.deleteByChatRoom(chatRoom) // 채팅 참여자 삭제
            chatRoomRepository.delete(chatRoom) // 채팅방 삭제
            return
        } else {
            chatParticipantRepository.delete(participant) // 해당 관리자만 삭제
        }

        // 모든 관리자가 나갔는지 확인 후 occupied 상태 변경
        if (chatParticipantRepository.countByChatRoomAndIsAdminTrueAndIsActiveTrue(chatRoom) == 0L) {
            chatRoom.occupied = false
            chatRoomRepository.save(chatRoom)
        }
    }
}