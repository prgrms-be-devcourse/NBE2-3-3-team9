package com.example.nbe233team9.domain.user.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.user.dto.UpdateUserDTO
import com.example.nbe233team9.domain.user.dto.UserDetailResponseDTO
import com.example.nbe233team9.domain.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService
) {


    @Operation(summary = "일반 회원 정보 조회")
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    fun getUserInfo(@AuthenticationPrincipal userDetails: CustomUserDetails): ApiResponse<UserDetailResponseDTO> {
        val user = userService.getUserInfo(userDetails.getUserId());
        return ApiResponse.ok(user);
    }

    @Operation(summary = "회원탈퇴")
    @DeleteMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    fun delete(@AuthenticationPrincipal userDetails: CustomUserDetails) :ApiResponse<String> {
        val delete = userService.deleteUser(userDetails.getUserId());
        return ApiResponse.ok(delete)
    }

    @Operation(summary = "일반 회원 정보 수정")
    @PutMapping
    @PreAuthorize("hasRole('USER')")
    fun userUpdate(@AuthenticationPrincipal userDetails: CustomUserDetails, @Valid @RequestBody updateUserDTO: UpdateUserDTO) : ApiResponse<String> {
        val user = userService.userUpdate(userDetails.getUserId(), updateUserDTO);
        return ApiResponse.ok(user);
    }






}