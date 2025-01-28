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

/**
 * 지금은 시스템 메시지의 receiver의 경우
 * 채팅방 참여자 중 처음으로 발견되는 사람으로 설정되는데,
 * 얘를 채팅방 참여자 각각을 receiver로 하게 2번 저장하되
 * 메시지 발행은 한 번만 되도록 해야 할지
 * 아니면, 그냥 기존 방식을 유지해야 할지 고민중
 * (어차피 receiver가 누구든 그 채팅방에 발행은 되니까
 * 크게 문제 없을 것 같긴 해서 ..)
 */