package com.example.nbe233team9.domain.schedule.model

import com.example.nbe233team9.domain.pet.model.Pet
import com.example.nbe233team9.domain.user.model.User
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
class PeriodicSchedule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private val pet: Pet,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private val user: User,

    @Column(name = "name", nullable = false)
    private var name: String,

    @Column(name = "start_date", nullable = false)
    private var startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    private var endDate: LocalDate,

    @Column(name = "start_time", nullable = false)
    private var startTime: LocalTime,

    @Column(name = "end_time", nullable = false)
    private var endTime: LocalTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_pattern", nullable = false)
    private var repeatPattern: RepeatPattern,

    @Column(name = "repeat_interval", nullable = false)
    private var repeatInterval: Int,

    @Column(name = "repeat_days")
    private var repeatDays: String? = null
) {
    @OneToMany(mappedBy = "periodicSchedule", cascade = [CascadeType.REMOVE])
    private val singleSchedules: List<SingleSchedule>? = null
}