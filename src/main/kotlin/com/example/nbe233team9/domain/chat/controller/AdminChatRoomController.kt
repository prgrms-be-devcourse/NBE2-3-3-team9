package com.example.nbe233team9.domain.chat.controller

import com.example.nbe233team9.common.dto.PageDTO
import com.example.nbe233team9.common.dto.PageRequestDTO
import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.chat.dto.ChatRoomResponseDTO
import com.example.nbe233team9.domain.chat.service.ChatParticipantService
import com.example.nbe233team9.domain.chat.service.ChatRoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Admin Chat Room", description = "관리자 전용 채팅방 관리 API")
@RestController
@RequestMapping("/api/admin/chat")
class AdminChatRoomController(
    private val chatRoomService: ChatRoomService,
    private val chatParticipantService: ChatParticipantService
) {

    @Operation(summary = "대기 중인 채팅방 조회")
    @GetMapping("/rooms/waiting")
    @PreAuthorize("hasRole('ADMIN')")
    fun getWaitingRooms(@ModelAttribute pageRequestDTO: PageRequestDTO): PageDTO<ChatRoomResponseDTO> {
        return chatRoomService.getWaitingRooms(pageRequestDTO)
    }

    @Operation(summary = "모든 채팅방 조회")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllChatRooms(
        @ModelAttribute pageRequestDTO: PageRequestDTO,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<PageDTO<ChatRoomResponseDTO>> {
        val adminId = userDetails.getUserId()
        return ApiResponse.ok(chatRoomService.getAllChatRooms(adminId, pageRequestDTO))
    }

    @Operation(summary = "채팅방 검색 (Admin)")
    @GetMapping("/rooms/search")
    @PreAuthorize("hasRole('ADMIN')")
    fun searchChatRooms(
        @RequestParam keyword: String,
        @ModelAttribute pageRequestDTO: PageRequestDTO
    ): ApiResponse<PageDTO<ChatRoomResponseDTO>> {
        return ApiResponse.ok(chatRoomService.searchAllChatRooms(keyword, pageRequestDTO))
    }

    @Operation(summary = "채팅방 입장 (Admin)", description = "관리자가 채팅방에 입장합니다.")
    @PostMapping("/rooms/{roomId}/enter")
    @PreAuthorize("hasRole('ADMIN')")
    fun enterChatRoomAsAdmin(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable roomId: String
    ): ApiResponse<String> {
        val adminId = userDetails.getUserId()
        chatParticipantService.joinChatRoom(roomId, adminId, true)
        return ApiResponse.ok("채팅방에 입장했습니다.")
    }

    @Operation(summary = "채팅방 퇴장 (Admin)", description = "관리자가 채팅방에서 나갑니다.")
    @DeleteMapping("/rooms/{roomId}/exit")
    fun exitChatRoomAsAdmin(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable roomId: String
    ):  ApiResponse<String> {
        val adminId = userDetails.getUserId()
        chatParticipantService.leaveChatRoom(roomId, adminId, true)
        return ApiResponse.ok("채팅방에서 성공적으로 퇴장했습니다.")
    }
}