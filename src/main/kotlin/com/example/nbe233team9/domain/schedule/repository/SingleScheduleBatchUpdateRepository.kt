package com.example.nbe233team9.domain.schedule.repository

import jakarta.transaction.Transactional
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SingleScheduleBatchUpdateRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    @Transactional
    fun updateNotificationTime(scheduleIds: List<Long>, notificatedAt: LocalDateTime) {
        val sql = "UPDATE single_schedule SET notificated_at = ? WHERE id = ?"
        val batchSize = 1000  // 배치 크기 설정

        // 1000개씩 나누어 처리
        scheduleIds.chunked(batchSize).forEach { batch ->
            jdbcTemplate.batchUpdate(sql, batch.map { id -> arrayOf(notificatedAt, id) })
        }
    }
}
