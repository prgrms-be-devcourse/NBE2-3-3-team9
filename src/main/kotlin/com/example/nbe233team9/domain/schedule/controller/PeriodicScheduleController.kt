package com.example.nbe233team9.domain.schedule.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.schedule.dto.PeriodicScheduleDTO
import com.example.nbe233team9.domain.schedule.service.PeriodicScheduleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class PeriodicScheduleController(
    private val periodicScheduleService: PeriodicScheduleService
) {
    @GetMapping("/periodicSchedules")
    fun findPeriodicSchedules(): ApiResponse<List<PeriodicScheduleDTO>> {
        val periodicScheduleDTOs: List<PeriodicScheduleDTO> = periodicScheduleService.findPeriodicSchedules(4L)
        return ApiResponse.ok(periodicScheduleDTOs)
    }

    @PostMapping("/periodicSchedule")
    fun addPeriodicSchedule(
        @RequestBody request: PeriodicScheduleDTO.AddPeriodicScheduleDTO): ApiResponse<PeriodicScheduleDTO> {
        val periodicScheduleDTO: PeriodicScheduleDTO = periodicScheduleService.addPeriodicSchedule(request,4L)
        return ApiResponse.ok(periodicScheduleDTO)
    }

    @PutMapping("/periodicSchedule/{scheduleId}")
    fun updatePeriodicSchedule(
        @RequestBody request: PeriodicScheduleDTO.UpdatePeriodicScheduleDTO): ApiResponse<PeriodicScheduleDTO> {
        val periodicScheduleDTO = periodicScheduleService.updatePeriodicSchedule(request, 4L)
        return ApiResponse.ok(periodicScheduleDTO)
    }

    @DeleteMapping("/periodicSchedule/{scheduleId}")
    fun deleteSingleSchedule(@PathVariable scheduleId: Long): ApiResponse<String> {
        periodicScheduleService.deletePeriodicSchedule(scheduleId)
        return ApiResponse.ok("삭제 성공")
    }
}