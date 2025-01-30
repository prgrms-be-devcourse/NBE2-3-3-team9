package com.example.nbe233team9.domain.schedule.model

import com.example.nbe233team9.domain.pet.model.Pet
import com.example.nbe233team9.domain.user.model.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class SingleSchedule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    private var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private val pet: Pet,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private val user: User,

    @ManyToOne
    @JoinColumn(name = "periodic_schedule_id")
    private val periodicSchedule: PeriodicSchedule? = null,

    @Column(name = "name", nullable = false)
    private var name: String,

    @Column(name = "start_datetime", nullable = false)
    private var startDatetime: LocalDateTime,

    @Column(name = "end_datetime", nullable = false)
    private var endDatetime: LocalDateTime,

    @Column(name = "notificated_at", nullable = true)
    private var notificatedAt: LocalDateTime? = null,
) {
}