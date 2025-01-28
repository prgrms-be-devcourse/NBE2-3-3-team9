package com.example.nbe233team9.domain.chat.entity

import com.example.nbe233team9.domain.user.model.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "chat_rooms")
class ChatRoom (

    // 채팅방 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    // 채팅방 고유 식별자 (UUID 형태)
    @Column(name = "room_id", nullable = false, unique = true)
    var roomId: String = generateUniqueRoomId(),

    // 채팅방 이름
    @Column(name = "room_name", nullable = false)
    var roomName: String,

    // 채팅방 설명
    @Column(name = "description", nullable = false)
    var description: String,

    // 관리자가 채팅방에 참여 중인지 여부
    @Column(name = "occupied", nullable = false)
    var occupied: Boolean = false,

    // 채팅방 생성자 (일반 사용자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    var creator: User,

    // 채팅방에 참여 중인 관리자 목록
    // (하나의 채팅방에는 여러 관리자가 참여할 수 있고, 하나의 관리자는 여러 채팅방에 참여할 수 있음)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "chat_room_admins",
        joinColumns = [JoinColumn(name = "chat_room_id")],
        inverseJoinColumns = [JoinColumn(name = "admin_id")]
    )
    var admins: MutableList<User> = mutableListOf(),

    // 마지막으로 전송된 메시지 내용
    @Column(name = "last_message")
    var lastMessage: String? = null,

    // 마지막 메시지 전송 시간
    @Column(name = "last_message_time")
    var lastMessageTime: LocalDateTime? = null,

    // 채팅방 생성 시간
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    // 채팅방 정보가 마지막으로 수정된 시간
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
) {

    /**
     * 엔티티가 업데이트되기 전에 실행되는 메서드
     * 채팅방 정보가 수정될 때마다 수정 시간을 현재 시간으로 자동 업데이트함
     */
    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }

    companion object {
        /**
         * 고유한 채팅방 ID(UUID)를 생성하는 메서드
         */
        fun generateUniqueRoomId(): String {
            return UUID.randomUUID().toString()
        }
    }
}
