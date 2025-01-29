package com.example.nbe233team9.domain.community.controller

import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.community.dto.CommentRequestDTO
import com.example.nbe233team9.domain.community.dto.CommentResponseDTO
import com.example.nbe233team9.domain.community.dto.LikeResponseDTO
import com.example.nbe233team9.domain.community.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping("/comments/{postingId}")
    //@PreAuthorize("hasRole('USER')")
    fun createComment(
        //@AuthenticationPrincipal userDetails: CustomUserDetails,
        userId : Long,
        @PathVariable postingId: Long,
        @RequestParam(required = false) parentId: Long?,
        @RequestBody commentRequestDTO: CommentRequestDTO
    ): ResponseEntity<CommentResponseDTO> {
        val commentResponseDTO = commentService.createComment(userId, postingId, parentId, commentRequestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDTO)
    }

    @PutMapping("/comments/{commentId}")
    //@PreAuthorize("hasRole('USER')")
    fun updateComment(
        @PathVariable commentId: Long,
        @RequestBody commentRequestDTO: CommentRequestDTO
    ): ResponseEntity<CommentResponseDTO> {
        val commentResponseDTO = commentService.updateComment(commentId, commentRequestDTO)
        return ResponseEntity.ok(commentResponseDTO)
    }

    @DeleteMapping("/comments/{commentId}")
    //@PreAuthorize("hasRole('USER')")
    fun deleteComment(@PathVariable commentId: Long): ResponseEntity<Void> {
        commentService.deleteComment(commentId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/like/{postingId}")
    //@PreAuthorize("hasRole('USER')")
    fun createLike(
        //@AuthenticationPrincipal userDetails: CustomUserDetails,
        userId : Long,
        @PathVariable postingId: Long
    ): ResponseEntity<LikeResponseDTO> {
        val likeResponseDTO = commentService.createLike(userId, postingId)
        return ResponseEntity.status(HttpStatus.CREATED).body(likeResponseDTO)
    }

    @DeleteMapping("/like/{postingId}")
    //@PreAuthorize("hasRole('USER')")
    fun deleteLike(
        //@AuthenticationPrincipal userDetails: CustomUserDetails,
        userId : Long,
        @PathVariable postingId: Long
    ): ResponseEntity<Void> {
        commentService.deleteLike(userId, postingId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/comments/{parentId}/replies")
    fun getReplies(
        //@AuthenticationPrincipal userDetails: CustomUserDetails,
        userId : Long,
        @PathVariable parentId: Long
    ): ResponseEntity<List<CommentResponseDTO>> {
        val replies = commentService.getReplies(userId, parentId)
        return ResponseEntity.ok(replies)
    }
}