package com.example.nbe233team9.domain.chat.repository
import com.example.nbe233team9.domain.chat.entity.ChatMessage
import com.example.nbe233team9.domain.chat.entity.ChatRoom
import com.example.nbe233team9.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
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

    // 특정 사용자가 보낸 메시지 조회
    fun findBySender(sender: User): List<ChatMessage>

    // 특정 사용자가 받은 메시지 조회
    fun findByReceiver(receiver: User): List<ChatMessage>

    // 특정 채팅방에서 특정 유형의 메시지 조회 (ENTER, TALK, EXIT)
    fun findByChatRoomAndType(chatRoom: ChatRoom, type: ChatMessage.MessageType): List<ChatMessage>
}