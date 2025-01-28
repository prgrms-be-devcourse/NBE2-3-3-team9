package com.example.nbe233team9.domain.chat.dto

import jakarta.validation.constraints.NotBlank

class ChatRoomCreateRequestDTO(
    @field:NotBlank(message = "채팅방 이름은 필수입니다.")
    var roomName: String,

    @field:NotBlank(message = "채팅방 설명은 필수입니다.")
    var description: String
)