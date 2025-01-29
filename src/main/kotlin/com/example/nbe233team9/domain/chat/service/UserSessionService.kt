package com.example.nbe233team9.domain.chat.service

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class UserSessionService(
    private val userStatusNotifier: UserStatusNotifier,
    private val redisTemplate: RedisTemplate<String, String>
) {

    private val log = LoggerFactory.getLogger(UserSessionService::class.java)

    companion object {
        private const val USER_STATUS_KEY = "user_status"
        private const val STATUS_CONNECTED = "connected"
        private const val STATUS_DISCONNECTED = "disconnected"
    }

    /**
     * 사용자가 연결되었을 때 상태 업데이트 및 실시간 전송
     */
    fun setUserConnected(userId: String) {
        updateUserStatus(userId, STATUS_CONNECTED)
        userStatusNotifier.notifyUserStatusChange(userId, STATUS_CONNECTED)
    }

    /**
     * 사용자가 연결 해제되었을 때 상태 업데이트 및 실시간 전송
     */
    fun setUserDisconnected(userId: String) {
        deleteUserStatus(userId)  // 🔥 Redis에서 삭제
        userStatusNotifier.notifyUserStatusChange(userId, STATUS_DISCONNECTED)
    }

    /**
     * Redis에 사용자 상태 업데이트
     */
    private fun updateUserStatus(userId: String, status: String) {
        runCatching {
            redisTemplate.opsForHash<String, String>().put(USER_STATUS_KEY, userId, status)
        }.onFailure { e ->
            log.error("사용자 상태 업데이트 실패 ID {}: {}", userId, e.message, e)
        }
    }

    /**
     * Redis에서 사용자 상태 삭제
     */
    private fun deleteUserStatus(userId: String) {
        runCatching {
            redisTemplate.opsForHash<String, String>().delete(USER_STATUS_KEY, userId)
            log.info("Redis에서 사용자 상태 삭제됨: {}", userId)
        }.onFailure { e ->
            log.error("Redis 상태 삭제 실패: 사용자 ID = {}", userId, e)
        }
    }

    /**
     * Ping을 받을 때마다 Redis TTL을 갱신하는 메서드
     */
    fun refreshUserStatus(userId: String) {
        runCatching {
            redisTemplate.expire(USER_STATUS_KEY, Duration.ofMinutes(1))  // TTL 1분 설정
            log.info("TTL 갱신: 사용자 ID = {}", userId)
        }.onFailure { e ->
            log.error("TTL 갱신 실패: 사용자 ID = {}", userId, e)
        }
    }
}