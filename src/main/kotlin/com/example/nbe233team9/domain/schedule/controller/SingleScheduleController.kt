package com.example.nbe233team9.domain.schedule.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.schedule.dto.SingleScheduleDTO
import com.example.nbe233team9.domain.schedule.service.SingleScheduleService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class SingleScheduleController(
    private val singleScheduleService: SingleScheduleService
) {
    @GetMapping("/singleSchedules")
    @PreAuthorize("hasRole('USER')")
    fun findSingleSchedules(@AuthenticationPrincipal userDetails: CustomUserDetails): ApiResponse<List<SingleScheduleDTO>> {
        val singleScheduleDTOs: List<SingleScheduleDTO> = singleScheduleService.findSingleSchedules(userDetails.getUserId())
        return ApiResponse.ok(singleScheduleDTOs)
    }

    @PostMapping("/singleSchedule")
    @PreAuthorize("hasRole('USER')")
    fun addSingleSchedule(
        @RequestBody request: SingleScheduleDTO.AddSingleScheduleDTO,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<SingleScheduleDTO> {
        val singleScheduleDTO: SingleScheduleDTO = singleScheduleService.addSingleSchedule(request, userDetails.getUserId())
        return ApiResponse.ok(singleScheduleDTO)
    }

    @PutMapping("/singleSchedule/{scheduleId}")
    @PreAuthorize("hasRole('USER')")
    fun updateSingleSchedule(
        @RequestBody request: SingleScheduleDTO.UpdateSingleScheduleDTO,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<SingleScheduleDTO> {
        val singleScheduleDTO: SingleScheduleDTO = singleScheduleService.updateSingleSchedule(request, userDetails.getUserId())
        return ApiResponse.ok(singleScheduleDTO)
    }

    @DeleteMapping("/singleSchedule/{scheduleId}")
    fun deleteSingleSchedule(@PathVariable scheduleId: Long): ApiResponse<String> {
        singleScheduleService.deleteSingleSchedule(scheduleId)
        return ApiResponse.ok("삭제 완료")
    }
}