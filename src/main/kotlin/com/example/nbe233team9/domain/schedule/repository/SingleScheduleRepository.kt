package com.example.nbe233team9.domain.schedule.repository

import com.example.nbe233team9.domain.schedule.model.PeriodicSchedule
import com.example.nbe233team9.domain.schedule.model.SingleSchedule
import com.example.nbe233team9.domain.user.model.User
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SingleScheduleRepository : JpaRepository<SingleSchedule, Long>, SingleScheduleRepositoryCustom {
    @Query("select s from SingleSchedule s where s.user = :user")
    fun findSingleSchedulesByUser(user: User): List<SingleSchedule>

    @Modifying
    @Query("delete SingleSchedule s where s.periodicSchedule = :periodicSchedule")
    fun deleteByPeriodicSchedule(periodicSchedule: PeriodicSchedule)

    @Query("select s.periodicSchedule from SingleSchedule s where s.id = :singleScheduleId")
    fun findPeriodicScheduleById(singleScheduleId: Long): PeriodicSchedule

    @Query("select count(s) from SingleSchedule s where s.periodicSchedule = :periodicSchedule")
    fun countByPeriodicScheduleId(periodicSchedule: PeriodicSchedule): Int

    @Query("""
    SELECT s 
    FROM SingleSchedule s 
    WHERE s.startDatetime BETWEEN :now AND :tenMinutesLater 
      AND s.notificatedAt IS NULL
""")
    fun findSchedulesWithinNextTenMinutes(
        @Param("now") now: LocalDateTime,
        @Param("tenMinutesLater") tenMinutesLater: LocalDateTime,
        pageable: Pageable
    ): Page<SingleSchedule>


}