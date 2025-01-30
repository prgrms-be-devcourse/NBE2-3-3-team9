package com.example.nbe233team9.domain.schedule.dto

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDateTime


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
        private val id: Long,
        private val petId: Long,
        private val name: String,
        private val startDatetime: LocalDateTime,
        private val endDatetime: LocalDateTime,
        private val petName: String? = null
    ) {
    }
}
