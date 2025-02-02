package com.example.nbe233team9.domain.chat.config

import com.example.nbe233team9.domain.chat.service.RedisMessageSubscriber
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.messaging.simp.SimpMessagingTemplate

/**
 * RedisConfig 클래스
 * - Redis의 Pub/Sub 설정, RedisTemplate 직렬화 및 ListenerContainer 설정을 관리함
 */
@Configuration
class RedisConfig(
    private val objectMapper: ObjectMapper,
    private val messagingTemplate: SimpMessagingTemplate,
    @Value("\${spring.data.redis.host}") private val redisHost: String,
    @Value("\${spring.data.redis.port}") private val redisPort: Int,
    @Value("\${spring.data.redis.password}") private val redisPassword: String,
    @Value("\${redis.channel.topic}") private val redisChannelTopic: String
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisConfiguration = RedisStandaloneConfiguration().apply {
            hostName = redisHost
            port = redisPort
            password = RedisPassword.of(redisPassword)
        }
        return LettuceConnectionFactory(redisConfiguration)
    }

    /**
     * Redis Pub/Sub 채널 주제 정의
     */
    @Bean
    fun topic(): ChannelTopic = ChannelTopic(redisChannelTopic)

    /**
     * RedisMessageListenerContainer 설정
     */
    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        redisMessageSubscriberAdapter: MessageListenerAdapter,
        topic: ChannelTopic
    ): RedisMessageListenerContainer {
        return RedisMessageListenerContainer().apply {
            setConnectionFactory(connectionFactory)
            addMessageListener(redisMessageSubscriberAdapter, topic)
        }
    }

    /**
     * RedisMessageSubscriber를 MessageListenerAdapter로 래핑
     */
    @Bean
    fun redisMessageSubscriberAdapter(): MessageListenerAdapter {
        val redisMessageSubscriber = RedisMessageSubscriber(objectMapper, messagingTemplate)
        return MessageListenerAdapter(redisMessageSubscriber, "handleMessage")
    }

    /**
     * 채팅 메시지 RedisTemplate
     */
    @Bean(name = ["chatMessageRedisTemplate"])
    fun chatMessageRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = Jackson2JsonRedisSerializer(Any::class.java)
        }
    }

    /**
     * 채팅방 RedisTemplate
     */
    @Bean(name = ["chatRoomRedisTemplate"])
    fun chatRoomRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = Jackson2JsonRedisSerializer(Any::class.java)
        }
    }

    /**
     * 기본 RedisTemplate
     */
    @Bean(name = ["defaultRedisTemplate"])
    fun defaultRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
        }
    }
}