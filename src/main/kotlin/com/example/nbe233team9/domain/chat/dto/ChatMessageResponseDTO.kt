package com.example.nbe233team9.domain.chat.dto

import com.example.nbe233team9.domain.chat.entity.ChatMessage
import java.time.LocalDateTime

class ChatMessageResponseDTO (
    var messageId: Long,
    var roomId: String,
    var senderName: String,
    var content: String,
    var type: ChatMessage.MessageType,
    var sentAt: LocalDateTime,
    var opponentId: Long? = null, // 상대방 정보
    var opponentName: String? = null,
    var opponentProfileImg: String? = null
)