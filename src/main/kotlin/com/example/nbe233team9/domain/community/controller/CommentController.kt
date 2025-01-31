package com.example.nbe233team9.domain.community.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.community.dto.CommentRequestDTO
import com.example.nbe233team9.domain.community.dto.CommentResponseDTO
import com.example.nbe233team9.domain.community.dto.LikeResponseDTO
import com.example.nbe233team9.domain.community.service.CommentService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping("/comments/{postingId}")
    @PreAuthorize("hasRole('USER')")
    fun createComment(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable postingId: Long,
        @RequestParam(required = false) parentId: Long?,
        @RequestBody commentRequestDTO: CommentRequestDTO
    ): ApiResponse<CommentResponseDTO> {
        val commentResponseDTO = commentService.createComment(userDetails.getUserId(), postingId, parentId, commentRequestDTO)
        return ApiResponse.created(commentResponseDTO)
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER')")
    fun updateComment(
        @PathVariable commentId: Long,
        @RequestBody commentRequestDTO: CommentRequestDTO
    ): ApiResponse<CommentResponseDTO> {
        val commentResponseDTO = commentService.updateComment(commentId, commentRequestDTO)
        return ApiResponse.ok(commentResponseDTO)
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER')")
    fun deleteComment(@PathVariable commentId: Long): ApiResponse<String> {
        commentService.deleteComment(commentId)
        return ApiResponse.ok("댓글 삭제 성공")
    }

    @PostMapping("/like/{postingId}")
    @PreAuthorize("hasRole('USER')")
    fun createLike(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable postingId: Long
    ): ApiResponse<LikeResponseDTO> {
        val likeResponseDTO = commentService.createLike(userDetails.getUserId(), postingId)
        return ApiResponse.created(likeResponseDTO)
    }

    @DeleteMapping("/like/{postingId}")
    @PreAuthorize("hasRole('USER')")
    fun deleteLike(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable postingId: Long
    ): ApiResponse<String> {
        commentService.deleteLike(userDetails.getUserId(), postingId)
        return ApiResponse.ok("좋아요 삭제 성공")
    }

    @GetMapping("/comments/{parentId}/replies")
    fun getReplies(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable parentId: Long
    ): ApiResponse<List<CommentResponseDTO>> {
        val replies = commentService.getReplies(userDetails.getUserId(), parentId)
        return ApiResponse.ok(replies)
    }
}