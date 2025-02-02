package com.example.nbe233team9.domain.chat.repository

import com.example.nbe233team9.domain.chat.entity.ChatRoom
import com.example.nbe233team9.domain.user.model.User
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * 생성자, 관리자 참여 여부 등 채팅방 관리
 */
@Repository
interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {

    // roomId로 채팅방 조회
    fun findByRoomId(roomId: String): Optional<ChatRoom>

    // 관리자가 없는(occupied = false) 대기 중인 채팅방 조회
    fun findByOccupiedFalse(pageable: Pageable): Page<ChatRoom>

    // 특정 사용자가 생성한 채팅방 목록 조회 (User ID 기준)
    fun findByCreatorId(userId: Long, pageable: Pageable): Page<ChatRoom>

    // 사용자가 생성한 채팅방 페이징 검색
    fun findByCreatorIdAndRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        creatorId: Long,
        roomNameKeyword: String,
        descriptionKeyword: String,
        pageable: Pageable
    ): Page<ChatRoom>

    // 사용자가 참여 중인 채팅방 페이징 검색
    fun findByRoomIdInAndRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        roomIds: List<String>,
        roomNameKeyword: String,
        descriptionKeyword: String,
        pageable: Pageable
    ): Page<ChatRoom>

    // 채팅방 이름 또는 설명에서 키워드 검색 (페이징 적용)
    fun findByRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        roomNameKeyword: String,
        descriptionKeyword: String,
        pageable: Pageable
    ): Page<ChatRoom>

    // 메시지에 포함된 채팅방 ID로 검색 (페이징 적용)
    fun findByRoomIdIn(
        roomIds: List<String>,
        pageable: Pageable
    ): Page<ChatRoom>

    // 특정 채팅방 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM ChatRoom c WHERE c.roomId = :roomId")
    fun deleteByRoomId(@Param("roomId") roomId: String)

    // 관리자가 참여 중인 채팅방 조회 (페이징 적용)
    @Query("SELECT DISTINCT cp.chatRoom FROM ChatParticipant cp WHERE cp.user = :admin AND cp.isAdmin = true")
    fun findAdminChatRooms(@Param("admin") admin: User, pageable: Pageable): Page<ChatRoom>
}
