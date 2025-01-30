package com.example.nbe233team9.domain.schedule.model

import com.example.nbe233team9.domain.pet.model.Pet
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
    val pet: Pet,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne
    @JoinColumn(name = "periodic_schedule_id")
    val periodicSchedule: PeriodicSchedule? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "start_datetime", nullable = false)
    var startDatetime: LocalDateTime,

    @Column(name = "end_datetime", nullable = false)
    var endDatetime: LocalDateTime,

    @Column(name = "notificated_at", nullable = true)
    var notificatedAt: LocalDateTime? = null,
) {
}