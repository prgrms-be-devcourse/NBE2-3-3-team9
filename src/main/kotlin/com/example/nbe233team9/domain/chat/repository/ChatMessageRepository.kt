package com.example.nbe233team9.domain.chat.repository
import com.example.nbe233team9.domain.chat.entity.ChatMessage
import com.example.nbe233team9.domain.chat.entity.ChatRoom
import com.example.nbe233team9.domain.user.model.User
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {

    // 메시지 내용에 키워드가 포함된 채팅방 ID 조회
    @Query("SELECT DISTINCT m.chatRoom.roomId FROM ChatMessage m WHERE m.content LIKE %:keyword%")
    fun findDistinctChatRoomIdsByKeyword(@Param("keyword") keyword: String): List<String>

    // 특정 채팅방의 모든 메시지 조회 (최신순)
    fun findByChatRoomOrderBySentAtAsc(chatRoom: ChatRoom): List<ChatMessage>

    // 특정 채팅방의 모든 메시지 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM ChatMessage m WHERE m.chatRoom = :chatRoom")
    fun deleteByChatRoom(@Param("chatRoom") chatRoom: ChatRoom)

    // 특정 키워드를 포함하고 특정 채팅방 ID 목록에 속하는 채팅방 ID 조회
    @Query("SELECT DISTINCT cm.chatRoom.id FROM ChatMessage cm WHERE cm.content LIKE %:keyword% AND cm.chatRoom.id IN :roomIds")
    fun findDistinctChatRoomIdsByKeywordAndRoomIds(
        @Param("keyword") keyword: String,
        @Param("roomIds") roomIds: List<String>
    ): List<String>
}