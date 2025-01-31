package com.example.nbe233team9.domain.schedule.service

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.domain.pet.model.Pet
import com.example.nbe233team9.domain.pet.repository.PetRepository
import com.example.nbe233team9.domain.schedule.dto.SingleScheduleDTO
import com.example.nbe233team9.domain.schedule.model.PeriodicSchedule
import com.example.nbe233team9.domain.schedule.model.SingleSchedule
import com.example.nbe233team9.domain.schedule.repository.PeriodicScheduleRepository
import com.example.nbe233team9.domain.schedule.repository.SingleScheduleRepository
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.repository.UserRepository
import org.springframework.stereotype.Service


@Service
class SingleScheduleService(
    private val singleScheduleRepository: SingleScheduleRepository,
    private val userRepository: UserRepository,
    private val petRepository: PetRepository,
    private val periodicScheduleRepository: PeriodicScheduleRepository
) {
    fun findSingleSchedules(userId: Long): List<SingleScheduleDTO> {
        val lists: List<SingleSchedule> = singleScheduleRepository.findSingleSchedulesByUser(getUserById(userId))

        if (lists.isEmpty()) {
            throw CustomException(ResultCode.NOT_EXISTS_SCHEDULE)
        }

        val singleScheduleDTOs: List<SingleScheduleDTO> = lists.map { singleSchedule: SingleSchedule ->
            SingleScheduleDTO.fromEntity(singleSchedule)
        }

        return singleScheduleDTOs
    }

    fun addSingleSchedule(request: SingleScheduleDTO.AddSingleScheduleDTO, userId: Long): SingleScheduleDTO {
        val petId: Long = request.petId

        if (!petRepository.existsById(petId)) {
            throw CustomException(ResultCode.NOT_EXISTS_PET)
        } else if (request.startDatetime.isAfter(request.endDatetime)) {
            throw CustomException(ResultCode.INVALID_DATETIME_VALUE)
        }

        val singleSchedule = SingleSchedule(
            name = request.name,
            pet = getPetById(request.petId),
            user = getUserById(userId),
            startDatetime = request.startDatetime,
            endDatetime = request.endDatetime
        )

        singleScheduleRepository.save(singleSchedule)

        return SingleScheduleDTO.fromEntity(singleSchedule)
    }

    fun updateSingleSchedule(request: SingleScheduleDTO.UpdateSingleScheduleDTO, userId: Long?): SingleScheduleDTO {
        val id: Long = request.id
        val petId: Long = request.petId

        if (petRepository.findById(petId).isEmpty) {
            throw CustomException(ResultCode.NOT_EXISTS_PET)
        } else if (request.startDatetime.isAfter(request.endDatetime)) {
            throw CustomException(ResultCode.INVALID_DATETIME_VALUE)
        }

        val singleSchedule: SingleSchedule = singleScheduleRepository.findById(id)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_SCHEDULE) }

        val periodicSchedule: PeriodicSchedule? = singleSchedule.periodicSchedule

        if (periodicSchedule != null) {
            if (singleScheduleRepository.countByPeriodicScheduleId(periodicSchedule!!) == 1) {
                periodicScheduleRepository.deleteById(periodicSchedule!!.id)
            }
        }

        singleSchedule.updateSingleSchedule(request, petRepository)

        singleScheduleRepository.save(singleSchedule)

        return SingleScheduleDTO.fromEntity(singleSchedule)
    }

    fun deleteSingleSchedule(scheduleId: Long) {
        val singleSchedule: SingleSchedule? = singleScheduleRepository.findById(scheduleId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_SCHEDULE) }

        val periodicSchedule: PeriodicSchedule? = singleSchedule?.periodicSchedule

        if (singleSchedule != null) {
            if (periodicSchedule != null) {
                if (singleScheduleRepository.countByPeriodicScheduleId(periodicSchedule!!) == 1) {
                    singleScheduleRepository.deleteById(scheduleId)
                    periodicScheduleRepository.deleteById(periodicSchedule!!.id)
                } else {
                    singleScheduleRepository.deleteById(scheduleId)
                }
            } else {
                singleScheduleRepository.deleteById(scheduleId)
            }
        }
    }

    private fun getUserById(userId: Long): User {
        return userRepository.findById(userId).orElseThrow { RuntimeException() }
    }

    private fun getPetById(petId: Long): Pet {
        return petRepository.findById(petId).orElseThrow { RuntimeException() }
    }
}
