package com.example.nbe233team9.domain.schedule.model

import com.example.nbe233team9.common.entities.CommonEntity
import com.example.nbe233team9.domain.pet.model.Pet
import com.example.nbe233team9.domain.pet.repository.PetRepository
import com.example.nbe233team9.domain.schedule.dto.SingleScheduleDTO
import com.example.nbe233team9.domain.user.model.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class SingleSchedule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    var pet: Pet,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne
    @JoinColumn(name = "periodic_schedule_id")
    var periodicSchedule: PeriodicSchedule? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "start_datetime", nullable = false)
    var startDatetime: LocalDateTime,

    @Column(name = "end_datetime", nullable = false)
    var endDatetime: LocalDateTime,

    @Column(name = "notificated_at", nullable = true)
    var notificatedAt: LocalDateTime? = null,
): CommonEntity() {

    fun updateSingleSchedule(request: SingleScheduleDTO.UpdateSingleScheduleDTO, petRe: PetRepository): SingleSchedule {
        this.name = request.name
        this.startDatetime = request.startDatetime
        this.endDatetime = request.endDatetime
        this.pet = petRe.findById(request.petId).orElseThrow { RuntimeException() }
        this.periodicSchedule = null

        return this
    }
}