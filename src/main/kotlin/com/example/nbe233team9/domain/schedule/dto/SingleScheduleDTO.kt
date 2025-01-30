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
    private val id: Long,
    private val petId: Long,
    private val petName: String,
    private val userId: Long,
    private val name: String,
    private val periodicScheduleId: Long? = null,
    private val startDatetime: LocalDateTime,
    private val endDatetime: LocalDateTime,
    private val createdAt: LocalDateTime,
    private val updatedAt: LocalDateTime,
    private val notificatedAt: LocalDateTime? = null
) {

    class AddSingleScheduleDTO(
        private val petId: Long,
        private val name: String,
        private val startDatetime: LocalDateTime,
        private val endDatetime: LocalDateTime,
        private val petName: String? = null
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
