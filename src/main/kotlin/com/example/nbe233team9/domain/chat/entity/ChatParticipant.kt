package com.example.nbe233team9.domain.chat.entity

import com.example.nbe233team9.domain.user.model.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_participants")
class ChatParticipant (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    /**
     * 참여한 채팅방 (ChatRoom과 다대일 관계)
     * 하나의 채팅방에 여러 명의 사용자가 참여할 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    val chatRoom: ChatRoom,

    /**
     * 참여자 정보 (User 또는 Admin)
     * User 엔티티와 연결되어 있고, Role을 통해 관리자 또는 일반 사용자 구분함
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "is_admin", nullable = false)
    val isAdmin: Boolean = false, // 관리자인지 아닌지 여부

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = false, // 현재 참여 중인지 여부

    @Column(name = "joined_at", nullable = false)
    var joinedAt: LocalDateTime? = null // 채팅방에 참여한 시각
){
    /**
     * 엔티티가 처음 저장되기 전에 실행되는 메서드
     * 채팅방 참여 시간을 현재 시간으로 자동 설정함
     */
    @PrePersist
    fun onJoin() {
        joinedAt = LocalDateTime.now()
    }
}