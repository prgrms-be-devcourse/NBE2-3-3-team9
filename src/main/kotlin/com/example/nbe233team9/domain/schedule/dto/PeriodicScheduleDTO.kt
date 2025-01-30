package com.example.nbe233team9.domain.schedule.dto

import com.example.nbe233team9.domain.schedule.model.RepeatPattern
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class PeriodicScheduleDTO(
    val id: Long,
    val userId: Long,
    val petId: Long,
    val petName: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val repeatPattern: RepeatPattern,
    val repeatInterval: Int,
    val repeatDays: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {

    class AddPeriodicScheduleDTO(
        val petId: Long,
        val petName: String? = null,
        val name: String,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val startTime: LocalTime,
        val endTime: LocalTime,
        val repeatPattern: RepeatPattern,
        val repeatInterval: Int,
        val repeatDays: String? = null,
    ) {
    }

    class UpdatePeriodicScheduleDTO(
        val id: Long,
        val petId: Long,
        val petName: String? = null,
        val name: String,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val startTime: LocalTime,
        val endTime: LocalTime,
        val repeatPattern: RepeatPattern,
        val repeatInterval: Int,
        val repeatDays: String? = null,
    ) {
    }
}
