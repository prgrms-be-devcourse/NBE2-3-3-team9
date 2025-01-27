package com.example.nbe233team9.domain.chat.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker // WebSocket 메시지 브로커 활성화
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    // 클라이언트가 WebSocket 연결을 요청할 때 사용할 엔드포인트 설정
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/chat-socket")
            .setAllowedOrigins("*")
            .withSockJS()
    }

    // 메시지 브로커 설정
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic/") // 클라이언트는 /topic/ 경로로 시작하는 주제를 구독하여 메시지를 받을 수 있음
        registry.setApplicationDestinationPrefixes("/app")     // 클라이언트가 서버로 메시지를 송신할 때 사용하는 경로 (@MessageMapping으로 전달됨)
    }

    /* 기존에 있던 configureClientInboundChannel은
     * 메시지 전송 시에는 jwt 검증을 안 하기로 해서 삭제
    */
}