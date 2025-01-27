package com.example.nbe233team9.domain.chat.dto

import com.example.nbe233team9.domain.chat.entity.ChatMessage
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class ChatMessageRequestDTO (
    @field:NotBlank(message = "메시지 내용은 필수입니다.")
    var content: String,

    @field:NotNull(message = "메시지 유형은 필수입니다.")
    var type: ChatMessage.MessageType,

    @field:NotBlank (message = "채팅방 ID는 필수입니다.")
    var roomId: String,

    var receivedId: Long? = null
){
}