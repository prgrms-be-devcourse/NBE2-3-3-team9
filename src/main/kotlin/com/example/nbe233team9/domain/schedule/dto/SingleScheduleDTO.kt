package com.example.nbe233team9.domain.schedule.dto

import com.example.nbe233team9.domain.schedule.model.SingleSchedule
import java.time.LocalDateTime

class SingleScheduleDTO(
    val id: Long,
    val petId: Long,
    val petName: String,
    val userId: Long,
    val name: String,
    val periodicScheduleId: Long? = null,
    val startDatetime: LocalDateTime,
    val endDatetime: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val notificatedAt: LocalDateTime? = null
) {

    class AddSingleScheduleDTO(
        val petId: Long,
        val name: String,
        val startDatetime: LocalDateTime,
        val endDatetime: LocalDateTime,
        val petName: String? = null
    ) {
    }


    class UpdateSingleScheduleDTO(
        val id: Long,
        val petId: Long,
        val name: String,
        val startDatetime: LocalDateTime,
        val endDatetime: LocalDateTime,
        val petName: String? = null
    ) {
    }

    companion object {
        fun fromEntity(singleSchedule: SingleSchedule): SingleScheduleDTO {
            return SingleScheduleDTO(
                id = singleSchedule.id,
                userId = singleSchedule.user.id!!,
                petId = singleSchedule.pet.id,
                petName = singleSchedule.pet.name,
                name = singleSchedule.pet.name,
                periodicScheduleId = singleSchedule.periodicSchedule?.id,
                startDatetime = singleSchedule.startDatetime,
                endDatetime = singleSchedule.endDatetime,
                notificatedAt = singleSchedule.notificatedAt,
                createdAt = singleSchedule.createdAt,
                updatedAt = singleSchedule.updatedAt
            )
        }
    }
}
