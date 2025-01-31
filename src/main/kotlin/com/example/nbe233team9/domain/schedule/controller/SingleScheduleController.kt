package com.example.nbe233team9.domain.schedule.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.schedule.dto.SingleScheduleDTO
import com.example.nbe233team9.domain.schedule.service.SingleScheduleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class SingleScheduleController(
    private val singleScheduleService: SingleScheduleService
) {
    @GetMapping("/singleSchedules")
    fun findSingleSchedules(): ApiResponse<List<SingleScheduleDTO>> {
        val singleScheduleDTOs: List<SingleScheduleDTO> = singleScheduleService.findSingleSchedules(4L)
        return ApiResponse.ok(singleScheduleDTOs)
    }

    @PostMapping("/singleSchedule")
    fun addSingleSchedule(
        @RequestBody request: SingleScheduleDTO.AddSingleScheduleDTO
    ): ApiResponse<SingleScheduleDTO> {
        val singleScheduleDTO: SingleScheduleDTO = singleScheduleService.addSingleSchedule(request, 4L)
        return ApiResponse.ok(singleScheduleDTO)
    }

    @PutMapping("/singleSchedule/{scheduleId}")
    fun updateSingleSchedule(
        @RequestBody request: SingleScheduleDTO.UpdateSingleScheduleDTO
    ): ApiResponse<SingleScheduleDTO> {
        val singleScheduleDTO: SingleScheduleDTO = singleScheduleService.updateSingleSchedule(request, 4L)
        return ApiResponse.ok(singleScheduleDTO)
    }

    @DeleteMapping("/singleSchedule/{scheduleId}")
    fun deleteSingleSchedule(@PathVariable scheduleId: Long): ApiResponse<String> {
        singleScheduleService.deleteSingleSchedule(scheduleId)
        return ApiResponse.ok("삭제 완료")
    }
}