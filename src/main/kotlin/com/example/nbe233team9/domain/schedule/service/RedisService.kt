package com.example.nbe233team9.domain.schedule.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Component
class RedisService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    fun setValues(key: String, data: String) {
        val values = redisTemplate.opsForValue()
        values[key] = data
    }

    fun setValues(key: String, data: String, duration: Duration) {
        val values = redisTemplate.opsForValue()
        values[key, data] = duration
    }

    @Transactional(readOnly = true)
    fun getValues(key: String): String? {
        val values = redisTemplate.opsForValue()
        if (values[key] == null) {
            return "false"
        }
        return values[key] as String
    }
}
