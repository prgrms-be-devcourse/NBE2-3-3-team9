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
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    val pet: Pet,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDate,

    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime,

    @Column(name = "end_time", nullable = false)
    var endTime: LocalTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_pattern", nullable = false)
    var repeatPattern: RepeatPattern,

    @Column(name = "repeat_interval", nullable = false)
    var repeatInterval: Int,

    @Column(name = "repeat_days")
    var repeatDays: String? = null
) {
    @OneToMany(mappedBy = "periodicSchedule", cascade = [CascadeType.REMOVE])
    val singleSchedules: List<SingleSchedule>? = null
}