package com.example.nbe233team9.domain.chat.dto

import java.time.LocalDateTime

class ChatRoomeResponseDTO (
    val roomId: String,                  // 채팅방 ID
    val roomName: String,                // 채팅방 이름
    val description: String,             // 채팅방 설명
    val occupied: Boolean,               // 관리자 참여 여부
    val lastMessage: String,             // 마지막 메시지
    val lastMessageTime: LocalDateTime, // 마지막 메시지 전송 시간
    val createdAt: LocalDateTime,       // 채팅방이 만들어진 시간

    // 상대방 정보
    val opponentId: Long?,
    val opponentName: String?,
    val opponentProfileImage: String?,
    val opponentStatus: String?)