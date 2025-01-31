package com.example.nbe233team9.domain.schedule.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.schedule.dto.PeriodicScheduleDTO
import com.example.nbe233team9.domain.schedule.service.PeriodicScheduleService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class PeriodicScheduleController(
    private val periodicScheduleService: PeriodicScheduleService
) {
    @GetMapping("/periodicSchedules")
    @PreAuthorize("hasRole('USER')")
    fun findPeriodicSchedules(@AuthenticationPrincipal userDetails: CustomUserDetails): ApiResponse<List<PeriodicScheduleDTO>> {
        val periodicScheduleDTOs: List<PeriodicScheduleDTO> = periodicScheduleService.findPeriodicSchedules(userDetails.getUserId())
        return ApiResponse.ok(periodicScheduleDTOs)
    }

    @PostMapping("/periodicSchedule")
    @PreAuthorize("hasRole('USER')")
    fun addPeriodicSchedule(
        @RequestBody request: PeriodicScheduleDTO.AddPeriodicScheduleDTO,
        @AuthenticationPrincipal userDetails: CustomUserDetails): ApiResponse<PeriodicScheduleDTO> {
        val periodicScheduleDTO: PeriodicScheduleDTO = periodicScheduleService.addPeriodicSchedule(request,userDetails.getUserId())
        return ApiResponse.ok(periodicScheduleDTO)
    }

    @PutMapping("/periodicSchedule/{scheduleId}")
    @PreAuthorize("hasRole('USER')")
    fun updatePeriodicSchedule(
        @RequestBody request: PeriodicScheduleDTO.UpdatePeriodicScheduleDTO,
        @AuthenticationPrincipal userDetails: CustomUserDetails): ApiResponse<PeriodicScheduleDTO> {
        val periodicScheduleDTO = periodicScheduleService.updatePeriodicSchedule(request, userDetails.getUserId())
        return ApiResponse.ok(periodicScheduleDTO)
    }

    @DeleteMapping("/periodicSchedule/{scheduleId}")
    fun deleteSingleSchedule(@PathVariable scheduleId: Long): ApiResponse<String> {
        periodicScheduleService.deletePeriodicSchedule(scheduleId)
        return ApiResponse.ok("삭제 성공")
    }
}