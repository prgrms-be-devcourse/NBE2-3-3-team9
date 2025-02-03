package com.example.nbe233team9.domain.schedule.repository

import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SingleScheduleRepositoryCustom {
    fun updateNotificationTime(scheduleIds: List<Long>, notificatedAt: LocalDateTime)
}