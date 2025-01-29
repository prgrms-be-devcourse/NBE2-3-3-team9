package com.example.nbe233team9.domain.chat.service

import com.example.nbe233team9.domain.chat.entity.ChatParticipant
import com.example.nbe233team9.domain.chat.entity.ChatRoom
import com.example.nbe233team9.domain.chat.repository.ChatParticipantRepository
import com.example.nbe233team9.domain.chat.repository.ChatRoomRepository
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.repository.UserRepository
import com.example.nbe233team9.global.constants.ChatConstants.SYSTEM_USER_EMAIL
import com.example.nbe233team9.global.constants.ChatConstants.SYSTEM_USER_ID
import com.example.nbe233team9.global.constants.ChatConstants.SYSTEM_USER_NAME
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

/**
 * 채팅 서비스에서 자주 사용되는 유틸성 메서드들을 모아둔 서비스 클래스
 * - 사용자 조회, 채팅방 조회, 참여자 검증 등의 기능 제공
 */
@Service
class ChatServiceUtil(
    private val userRepository: UserRepository,                     // 사용자 정보 조회를 위한 Repository
    private val chatRoomRepository: ChatRoomRepository,             // 채팅방 정보 조회를 위한 Repository
    private val chatParticipantRepository: ChatParticipantRepository, // 채팅방 참여자 정보 조회를 위한 Repository
    private val redisTemplate: RedisTemplate<String, String>        // Redis를 활용한 사용자 상태 관리
) {

    /**
     * 사용자 ID로 사용자 정보를 조회
     * @param userId 조회할 사용자의 ID
     * @return User 객체
     * @throws EntityNotFoundException 사용자가 존재하지 않을 경우 발생
     */
    fun findUserById(userId: Long): User =
        userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("사용자를 찾을 수 없습니다. ID: $userId") }


    /**
     * 채팅방 ID로 채팅방 정보를 조회
     * @param roomId 조회할 채팅방의 ID
     * @return ChatRoom 객체
     * @throws EntityNotFoundException 채팅방이 존재하지 않을 경우 발생
     */
    fun findChatRoomById(roomId: String): ChatRoom =
        chatRoomRepository.findByRoomId(roomId)
            .orElseThrow { EntityNotFoundException("채팅방을 찾을 수 없습니다. RoomID: $roomId") }


    /**
     * 시스템 메시지를 전송하기 위한 시스템 사용자 객체 생성
     * @return 시스템 사용자 (User 객체)
     */
    fun getSystemUser(): User =
        User(id = SYSTEM_USER_ID, name = SYSTEM_USER_NAME, email = SYSTEM_USER_EMAIL)


    /**
     * 사용자가 특정 채팅방에 참여 중인지 검증
     * @param sender 검증할 사용자
     * @param chatRoom 검증할 채팅방
     * @throws IllegalStateException 사용자가 채팅방에 참여 중이지 않을 경우 발생
     */
    fun validateParticipant(sender: User, chatRoom: ChatRoom) {
        val isParticipant = chatParticipantRepository.findByUserAndChatRoom(sender, chatRoom)
            .filter { it.isActive }
            .isPresent

        if (!isParticipant) {
            throw IllegalStateException("해당 사용자는 채팅방에 참여 중이지 않습니다. [UserID: ${sender.id}, RoomID: ${chatRoom.roomId}]")
        }
    }


    /**
     * 사용자와 채팅방 정보를 기반으로 참여자 정보 조회
     * @param user 조회할 사용자
     * @param chatRoom 조회할 채팅방
     * @return ChatParticipant 객체
     * @throws EntityNotFoundException 참여자가 존재하지 않을 경우 발생
     */
    fun findParticipantByUserAndRoom(user: User, chatRoom: ChatRoom): ChatParticipant =
        chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)
            .orElseThrow { EntityNotFoundException("참여자를 찾을 수 없습니다.") }


    /**
     * Redis에서 사용자의 현재 상태를 조회
     * @param userId 사용자 ID
     * @return 사용자 상태 (connected, disconnected, unknown)
     */
    fun getUserStatus(userId: String): String {
        return try {
            redisTemplate.opsForHash<String, String>().get("user_status", userId) ?: "disconnected"
        } catch (e: Exception) {
            "unknown"
        }
    }
}