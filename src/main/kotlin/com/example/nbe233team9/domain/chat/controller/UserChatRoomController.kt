package com.example.nbe233team9.domain.chat.controller

import com.example.nbe233team9.common.dto.PageDTO
import com.example.nbe233team9.common.dto.PageRequestDTO
import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.chat.dto.ChatRoomCreateRequestDTO
import com.example.nbe233team9.domain.chat.dto.ChatRoomResponseDTO
import com.example.nbe233team9.domain.chat.service.ChatParticipantService
import com.example.nbe233team9.domain.chat.service.ChatRoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "User Chat Room", description = "사용자 전용 채팅방 관리 API")
@RestController
@RequestMapping("/api/user/chat")
class UserChatRoomController(
    private val chatRoomService: ChatRoomService,
    private val chatParticipantService: ChatParticipantService
) {

    @Operation(summary = "채팅방 생성 (User)")
    @PostMapping("/rooms")
    @PreAuthorize("hasRole('USER')")
    fun createRoom(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestBody requestDTO: ChatRoomCreateRequestDTO
    ): ChatRoomResponseDTO {
        val userId = userDetails.getUserId()
        return chatRoomService.createChatRoom(userId, requestDTO)
    }

    @Operation(summary = "내 채팅방 조회")
    @GetMapping("/rooms")
    @PreAuthorize("hasRole('USER')")
    fun getMyChatRooms(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @ModelAttribute pageRequestDTO: PageRequestDTO
    ): PageDTO<ChatRoomResponseDTO> {
        val userId = userDetails.getUserId()
        return chatRoomService.getRoomsByUserId(userId, pageRequestDTO)
    }

    @Operation(summary = "내 채팅방 검색", description = "사용자가 본인이 참여한 채팅방을 검색")
    @GetMapping("/rooms/search")
    @PreAuthorize("hasRole('USER')")
    fun searchMyChatRooms(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam keyword: String,
        @ModelAttribute pageRequestDTO: PageRequestDTO
    ): PageDTO<ChatRoomResponseDTO> {
        val userId = userDetails.getUserId()
        return chatRoomService.searchUserChatRooms(userId, keyword, pageRequestDTO)
    }

    @Operation(summary = "채팅방 입장 (User)", description = "사용자가 채팅방에 입장합니다.")
    @PostMapping("/rooms/{roomId}/enter")
    @PreAuthorize("hasRole('USER')")
    fun enterChatRoom(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable roomId: String
    ): ResponseEntity<String> {
        val userId = userDetails.getUserId()

        // ✅ 사용자 채팅방 입장 처리
        chatParticipantService.joinChatRoom(roomId, userId, false)

        return ResponseEntity.ok("채팅방에 입장했습니다.")
    }

    @Operation(summary = "채팅방 퇴장 (User)", description = "사용자가 채팅방에서 나갑니다.")
    @DeleteMapping("/rooms/{roomId}/exit")
    fun exitChatRoom(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable roomId: String
    ): ResponseEntity<String> {
        val userId = userDetails.getUserId()

        chatParticipantService.leaveChatRoom(roomId, userId, false)

        return ResponseEntity.ok("채팅방에서 성공적으로 퇴장했습니다.")
    }
}