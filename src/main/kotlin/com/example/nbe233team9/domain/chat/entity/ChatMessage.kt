package com.example.nbe233team9.domain.chat.entity

import com.example.nbe233team9.domain.user.model.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_messages")
data class ChatMessage (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    var sender: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    var receiver: User? = null,

    @Column(name = "content", nullable = false, length = 1000)
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: MessageType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    var chatRoom: ChatRoom,

    @Column(name = "sent_at", nullable = false)
    var sentAt: LocalDateTime = LocalDateTime.now()
){
    @PrePersist
    fun onCreate(){
        this.sentAt = LocalDateTime.now()
    }

    enum class MessageType{
        TALK,
        SYSTEM
    }
}