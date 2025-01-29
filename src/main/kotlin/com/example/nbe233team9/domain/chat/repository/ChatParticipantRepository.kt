package com.example.nbe233team9.domain.chat.repository

import com.example.nbe233team9.domain.chat.entity.ChatParticipant
import com.example.nbe233team9.domain.chat.entity.ChatRoom
import com.example.nbe233team9.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChatParticipantRepository : JpaRepository<ChatParticipant, Long> {
    // 특정 채팅방의 참여자 목록 조회
    fun findByChatRoom(chatRoom: ChatRoom): List<ChatParticipant>

    // 특정 사용자의 참여 정보 조회
    fun findByUserAndChatRoom(user: User, chatRoom: ChatRoom): Optional<ChatParticipant>

    // 특정 채팅방에 참여 중인 관리자 목록 조회
    fun findByChatRoomAndIsAdminTrue(chatRoom: ChatRoom): List<ChatParticipant>

    // 채팅방에서 활성 상태인 관리자의 수를 반환
    fun countByChatRoomAndIsAdminTrueAndIsActiveTrue(chatRoom: ChatRoom): Long

    // 사용자가 참여 중인 채팅방 ID 목록 조회
    @Query("SELECT cp.chatRoom.roomId FROM ChatParticipant cp WHERE cp.user.id = :userId AND cp.isActive = true")
    fun findRoomIdsByUserId(@Param("userId") userId: Long): List<String>
}