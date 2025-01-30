package com.example.nbe233team9.domain.schedule.repository

import com.example.nbe233team9.domain.schedule.model.PeriodicSchedule
import com.example.nbe233team9.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PeriodicScheduleRepository : JpaRepository<PeriodicSchedule, Long> {
    @Query("select ps from PeriodicSchedule ps where ps.user = :user")
    fun findPeriodicSchedulesByUserId(user: User?): List<PeriodicSchedule>
}