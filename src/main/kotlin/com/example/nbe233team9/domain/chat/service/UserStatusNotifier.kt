package com.example.nbe233team9.domain.chat.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class UserStatusNotifier(
    private val messagingTemplate: SimpMessagingTemplate
) {

    /**
     * 사용자 상태 변경 시 프론트에 실시간 전송
     */
    fun notifyUserStatusChange(userId: String, status: String) {
        messagingTemplate.convertAndSend("/topic/user-status/$userId", status)
    }
}