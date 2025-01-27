package com.example.nbe233team9.domain.chat.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.messaging.simp.SimpMessagingTemplate

class RedisMessageSubscriber (
    private val objectMapper: ObjectMapper, // JSON 역직렬화
    private val messagingTemplate: SimpMessagingTemplate // WebSocket 메시지 전송
){

}