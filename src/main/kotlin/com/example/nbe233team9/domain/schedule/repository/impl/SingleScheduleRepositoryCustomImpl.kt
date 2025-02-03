package com.example.nbe233team9.domain.schedule.repository.impl

import com.example.nbe233team9.domain.schedule.repository.SingleScheduleRepositoryCustom
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SingleScheduleRepositoryCustomImpl: SingleScheduleRepositoryCustom {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional
    override fun updateNotificationTime(scheduleIds: List<Long>, notificatedAt: LocalDateTime) {
        entityManager.createQuery(
            "UPDATE SingleSchedule s SET s.notificatedAt = :notificatedAt WHERE s.id IN :ids"
        ).setParameter("notificatedAt", notificatedAt)
            .setParameter("ids", scheduleIds)
            .executeUpdate()
    }
}