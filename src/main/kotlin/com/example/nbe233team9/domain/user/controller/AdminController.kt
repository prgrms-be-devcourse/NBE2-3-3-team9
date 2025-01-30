package com.example.nbe233team9.domain.user.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.user.dto.AdminResponseDTO
import com.example.nbe233team9.domain.user.dto.CreateAdminDTO
import com.example.nbe233team9.domain.user.dto.UpdateAdminDTO
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.service.AdminService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService
) {


    @Operation(summary = "관리자 회원가입")
    @PostMapping
    fun signup(@RequestBody createAdminDTO: CreateAdminDTO): ApiResponse<AdminResponseDTO> {
        val adminResponse = adminService.createAdmin(createAdminDTO)
        return ApiResponse.ok(adminResponse)
    }


    @Operation(summary = "관리자 정보조회")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun adminProfile(@AuthenticationPrincipal userDetails: CustomUserDetails) :ApiResponse<User> {
        val user = adminService.adminInfo(userDetails.getUserId());

        return ApiResponse.ok(user);
    }

    @Operation(summary = "관리자 정보 수정")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminUpdate(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody updateAdminDTO: UpdateAdminDTO
    ): ApiResponse<String> {
        val result =adminService.adminUpdate(userDetails.getUserId(), updateAdminDTO)
        return ApiResponse.ok(result)
    }



}