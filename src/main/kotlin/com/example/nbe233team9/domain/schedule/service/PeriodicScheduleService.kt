package com.example.nbe233team9.domain.schedule.service

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.domain.pet.model.Pet
import com.example.nbe233team9.domain.pet.repository.PetRepository
import com.example.nbe233team9.domain.schedule.dto.PeriodicScheduleDTO
import com.example.nbe233team9.domain.schedule.model.PeriodicSchedule
import com.example.nbe233team9.domain.schedule.model.RepeatPattern
import com.example.nbe233team9.domain.schedule.model.SingleSchedule
import com.example.nbe233team9.domain.schedule.repository.PeriodicScheduleRepository
import com.example.nbe233team9.domain.schedule.repository.SingleScheduleRepository
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class PeriodicScheduleService(
    private val periodicScheduleRepo: PeriodicScheduleRepository,
    private val userRepository: UserRepository,
    private val petRepository: PetRepository,
    private val singleScheduleRepository: SingleScheduleRepository
) {
    fun findPeriodicSchedules(userId: Long): List<PeriodicScheduleDTO> {
        val lists: List<PeriodicSchedule> = periodicScheduleRepo.findPeriodicSchedulesByUserId(getUserById(userId))

        if (lists.isEmpty()) {
            throw CustomException(ResultCode.NOT_EXISTS_SCHEDULE)
        }

        val periodicScheduleDTOs: List<PeriodicScheduleDTO> =
            lists.map { periodicSchedule: PeriodicSchedule ->
                PeriodicScheduleDTO.fromEntity(periodicSchedule)
            }

        return periodicScheduleDTOs
    }

    @Transactional
    fun addPeriodicSchedule(request: PeriodicScheduleDTO.AddPeriodicScheduleDTO, userId: Long): PeriodicScheduleDTO {
        val petId: Long = request.petId

        if (!petRepository.existsById(petId)) {
            throw CustomException(ResultCode.NOT_EXISTS_PET)
        }
        if (request.startDate.isAfter(request.endDate)) {
            throw CustomException(ResultCode.INVALID_DATETIME_VALUE)
        }
        if (request.startTime.isAfter(request.endTime)) {
            throw CustomException(ResultCode.INVALID_DATETIME_VALUE)
        }
        if (request.repeatPattern == RepeatPattern.DAILY && request.repeatDays != null) {
            throw CustomException(ResultCode.INVALID_REQUEST)
        }
        if (request.repeatPattern == RepeatPattern.WEEKLY && request.repeatDays == null) {
            throw CustomException(ResultCode.MISSING_PARAMETER)
        }
        if (request.repeatInterval <= 0) {
            throw CustomException(ResultCode.INVALID_REQUEST)
        }

        val periodicSchedule = PeriodicSchedule(
            user = getUserById(userId),
            pet = getPetById(petId),
            name = request.name,
            startDate = request.startDate,
            endDate = request.endDate,
            startTime = request.startTime,
            endTime = request.endTime,
            repeatPattern = request.repeatPattern,
            repeatInterval = request.repeatInterval,
            repeatDays = request.repeatDays
        )


        periodicScheduleRepo.save(periodicSchedule)

        createSchedules(periodicSchedule)

        val periodicScheduleDTO: PeriodicScheduleDTO = PeriodicScheduleDTO.fromEntity(periodicSchedule)

        return periodicScheduleDTO
    }

    @Transactional
    fun updatePeriodicSchedule(request: PeriodicScheduleDTO.UpdatePeriodicScheduleDTO, userId: Long?): PeriodicScheduleDTO {
        val Id: Long = request.id
        val petId: Long = request.petId

        if (!petRepository.existsById(petId)) {
            throw CustomException(ResultCode.NOT_EXISTS_PET)
        }
        if (request.startDate.isAfter(request.endDate)) {
            throw CustomException(ResultCode.INVALID_DATETIME_VALUE)
        }
        if (request.startTime.isAfter(request.endTime)) {
            throw CustomException(ResultCode.INVALID_DATETIME_VALUE)
        }
        if (request.repeatPattern == RepeatPattern.DAILY && request.repeatDays != null) {
            throw CustomException(ResultCode.INVALID_REQUEST)
        }
        if (request.repeatPattern == RepeatPattern.WEEKLY && request.repeatDays == null) {
            throw CustomException(ResultCode.MISSING_PARAMETER)
        }
        if (request.repeatInterval <= 0) {
            throw CustomException(ResultCode.INVALID_REQUEST)
        }

        val periodicSchedule = periodicScheduleRepo.findById(Id)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_SCHEDULE) }

        periodicSchedule.updatePeriodicSchedule(request, petRepository)

        periodicScheduleRepo.save(periodicSchedule)

        singleScheduleRepository.deleteByPeriodicSchedule(periodicSchedule)
        createSchedules(periodicSchedule)

        val periodicScheduleDTO: PeriodicScheduleDTO = PeriodicScheduleDTO.fromEntity(periodicSchedule)

        return periodicScheduleDTO
    }

    fun deletePeriodicSchedule(scheduleId: Long) {
        if (periodicScheduleRepo.existsById(scheduleId)) {
            periodicScheduleRepo.deleteById(scheduleId)
        } else {
            throw CustomException(ResultCode.NOT_EXISTS_SCHEDULE)
        }
    }

    private fun getUserById(userId: Long): User {
        return userRepository.findById(userId).orElseThrow { RuntimeException() }
    }

    private fun getPetById(petId: Long): Pet {
        return petRepository.findById(petId).orElseThrow { RuntimeException() }
    }

    private fun createSchedules(periodicSchedule: PeriodicSchedule) {
        val schedules: MutableList<SingleSchedule> = ArrayList<SingleSchedule>()
        var currentDate: LocalDate = periodicSchedule.startDate

        if (periodicSchedule.repeatPattern == RepeatPattern.DAILY) {
            while (!currentDate.isAfter(periodicSchedule.endDate)) {
                schedules.add(createSchedule(periodicSchedule, currentDate))
                currentDate = currentDate.plusDays(periodicSchedule.repeatInterval.toLong())
            }
        } else if (periodicSchedule.repeatPattern == RepeatPattern.WEEKLY) {
            val targetDays = parseRepeatDays(periodicSchedule.repeatDays)
            var targetDate = currentDate
            while (!targetDate.isAfter(periodicSchedule.endDate)) {
                for (dayOfWeek in targetDays) {
                    val weekStart =
                        currentDate.minusDays((currentDate.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
                    targetDate = weekStart.plusDays((dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
                    if (!targetDate.isBefore(periodicSchedule.startDate) && !targetDate.isAfter(periodicSchedule.endDate)) {
                        schedules.add(createSchedule(periodicSchedule, targetDate))
                    }
                }
                currentDate = currentDate.plusWeeks(periodicSchedule.repeatInterval.toLong())
            }
        }

        // 스케줄 저장
        singleScheduleRepository.saveAll(schedules)
    }

    private fun createSchedule(periodicSchedule: PeriodicSchedule, date: LocalDate): SingleSchedule {
        val startDatetime = LocalDateTime.of(date, periodicSchedule.startTime)
        val endDatetime = LocalDateTime.of(date, periodicSchedule.endTime)

        return SingleSchedule(
            name = periodicSchedule.name,
            pet = periodicSchedule.pet,
            periodicSchedule = periodicSchedule,
            user = periodicSchedule.user,
            startDatetime = startDatetime,
            endDatetime = endDatetime
        )
    }

    private fun parseRepeatDays(repeatDays: String?): List<DayOfWeek> {
        val dayOfWeeks: MutableList<DayOfWeek> = ArrayList()
        if (repeatDays != null) {
            for (day in repeatDays.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                when (day.trim { it <= ' ' }.lowercase(Locale.getDefault())) {
                    "monday", "월" -> dayOfWeeks.add(DayOfWeek.MONDAY)
                    "tuesday", "화" -> dayOfWeeks.add(DayOfWeek.TUESDAY)
                    "wednesday", "수" -> dayOfWeeks.add(DayOfWeek.WEDNESDAY)
                    "thursday", "목" -> dayOfWeeks.add(DayOfWeek.THURSDAY)
                    "friday", "금" -> dayOfWeeks.add(DayOfWeek.FRIDAY)
                    "saturday", "토" -> dayOfWeeks.add(DayOfWeek.SATURDAY)
                    "sunday", "일" -> dayOfWeeks.add(DayOfWeek.SUNDAY)
                }
            }
        }
        return dayOfWeeks
    }
}