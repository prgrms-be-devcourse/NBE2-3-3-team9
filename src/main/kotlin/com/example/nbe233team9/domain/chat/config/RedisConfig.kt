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


@Configuration
class RedisConfig (
    private val objectMapper: ObjectMapper,
    private val messagingTemplate: SimpMessagingTemplate
){
    // Redis 호스트 주소
    @Value("\${spring.data.redis.host}")
    private lateinit var redisHost: String

    // Redis 포트 번호
    @Value("\${spring.data.redis.port}")
    private var redisPort: Int = 0

    // Redis 비밀번호
    @Value("\${spring.data.redis.password}")
    private lateinit var redisPassword: String

    // Redis Pub/Sub 채널 이름
    @Value("\${redis.channel.topic}")
    private lateinit var redisChannelTopic: String


    /**
     * RedisConnectionFactory
     * - Redis와의 연결을 생성하는 팩토리
     * - RedisStandaloneConfiguration을 사용하여 단일 노드 Redis 서버를 설정함
     */
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisConfiguration = RedisStandaloneConfiguration().apply {
            hostName = redisHost
            port = redisPort
            password = RedisPassword.of(redisPassword)
        }
        return LettuceConnectionFactory(redisConfiguration) // Lettuce 클라이언트를 사용한 연결 팩토리 생성
    }


    /**
     * ChannelTopic
     * - Redis Pub / Sub에서 사용할 채널 주제를 정의함
     */    @Bean
    fun topic(): ChannelTopic = ChannelTopic(redisChannelTopic)


    /**
     * RedisMessageListenerContainer
     * - Redis Pub / Sub 메시지를 리스닝하는 컨테이너
     * - 특정 채널 (topic)에 대해 메시지 리스너를 등록함
     *
     * @param connectionFactory Redis 연결 팩토리
     * @param redisMessageSubscriberAdapter 메시지 리스너 어댑터
     * @param topic Pub / Sub 채널 주제
     */
    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        redisMessageSubscriberAdapter: MessageListenerAdapter,
        topic: ChannelTopic
    ): RedisMessageListenerContainer {
        return RedisMessageListenerContainer().apply {
            setConnectionFactory(connectionFactory)                  // Redis 연결 팩토리 설정
            addMessageListener(redisMessageSubscriberAdapter, topic) // 메시지 리스너 등록
        }
    }


    /**
     * MessageListenerAdapter
     * - Redis 메시지를 수신하여 처리하는 어댑터
     * - 메시지를 처리할 `RedisMessageSubscriber`를 감싸는 역할
     */
    @Bean
    fun redisMessageSubscriberAdapter(): MessageListenerAdapter {
        // 메시지 구독자 생성 및 어댑터로 래핑
        val redisMessageSubscriber = RedisMessageSubscriber(objectMapper, messagingTemplate)
        return MessageListenerAdapter(redisMessageSubscriber, "handleMessage")
    }


    /**
     * RedisTemplate (채팅 메시지용)
     * - Redis에 채팅 메시지를 직렬화/역직렬화하여 저장 / 조회하기 위한 템플릿
     *
     * @param connectionFactory Redis 연결 팩토리
     * @return RedisTemplate<String, Any>
     */
    @Bean(name = ["chatMessageRedisTemplate"])
    fun chatMessageRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(connectionFactory) // Redis 연결 팩토리 설정
            keySerializer = StringRedisSerializer() // 키를 문자열로 직렬화
            valueSerializer = Jackson2JsonRedisSerializer(Any::class.java) // 값을 JSON으로 직렬화
        }
    }


    /**
     * RedisTemplate (채팅방 데이터용)
     * - Redis에 채팅방 데이터를 저장 / 조회하기 위한 템플릿
     */
    @Bean(name = ["chatRoomRedisTemplate"])
    fun chatRoomRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(connectionFactory) // Redis 연결 팩토리 설정
            keySerializer = StringRedisSerializer() // 키를 문자열로 직렬화
            valueSerializer = Jackson2JsonRedisSerializer(Any::class.java) // 값을 JSON으로 직렬화
        }
    }


    /**
     * RedisTemplate (범용)
     * - Redis에 다양한 객체를 저장 / 조회하기 위한 범용 템플릿
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