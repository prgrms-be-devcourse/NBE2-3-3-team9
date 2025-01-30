package com.example.nbe233team9.domain.schedule.dto

import com.example.nbe233team9.domain.schedule.model.RepeatPattern
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class PeriodicScheduleDTO(
    private val id: Long,
    private val userId: Long,
    private val petId: Long,
    private val petName: String,
    private val name: String,
    private val startDate: LocalDate,
    private val endDate: LocalDate,
    private val startTime: LocalTime,
    private val endTime: LocalTime,
    private val repeatPattern: RepeatPattern,
    private val repeatInterval: Int,
    private val repeatDays: String? = null,
    private val createdAt: LocalDateTime,
    private val updatedAt: LocalDateTime
) {

    class AddPeriodicScheduleDTO(
        private val petId: Long,
        private val petName: String? = null,
        private val name: String,
        private val startDate: LocalDate,
        private val endDate: LocalDate,
        private val startTime: LocalTime,
        private val endTime: LocalTime,
        private val repeatPattern: RepeatPattern,
        private val repeatInterval: Int,
        private val repeatDays: String? = null,
    ) {
    }

    class UpdatePeriodicScheduleDTO(
        private val id: Long,
        private val petId: Long,
        private val petName: String? = null,
        private val name: String,
        private val startDate: LocalDate,
        private val endDate: LocalDate,
        private val startTime: LocalTime,
        private val endTime: LocalTime,
        private val repeatPattern: RepeatPattern,
        private val repeatInterval: Int,
        private val repeatDays: String? = null,
    ) {
    }
}
