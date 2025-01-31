package com.example.nbe233team9.domain.schedule.dto

import com.example.nbe233team9.domain.schedule.model.PeriodicSchedule
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

    companion object {
        fun fromEntity(periodicSchedule: PeriodicSchedule): PeriodicScheduleDTO {
            return PeriodicScheduleDTO(
                id = periodicSchedule.id,
                userId = periodicSchedule.user.id!!,
                petId = periodicSchedule.pet.id,
                petName = periodicSchedule.pet.name,
                name = periodicSchedule.name,
                startDate = periodicSchedule.startDate,
                endDate = periodicSchedule.endDate,
                startTime = periodicSchedule.startTime,
                endTime = periodicSchedule.endTime,
                repeatPattern = periodicSchedule.repeatPattern,
                repeatInterval = periodicSchedule.repeatInterval,
                repeatDays = periodicSchedule.repeatDays,
                createdAt = periodicSchedule.createdAt,
                updatedAt = periodicSchedule.updatedAt
            )
        }
    }
}
