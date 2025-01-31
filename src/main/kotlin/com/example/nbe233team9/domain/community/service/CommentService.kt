package com.example.nbe233team9.domain.community.service

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.domain.community.dto.CommentRequestDTO
import com.example.nbe233team9.domain.community.dto.CommentResponseDTO
import com.example.nbe233team9.domain.community.dto.CommunityResponseDTO
import com.example.nbe233team9.domain.community.dto.LikeResponseDTO
import com.example.nbe233team9.domain.community.model.Comment
import com.example.nbe233team9.domain.community.model.CommunityLike
import com.example.nbe233team9.domain.community.repository.CommentRepository
import com.example.nbe233team9.domain.community.repository.CommunityLikeRepository
import com.example.nbe233team9.domain.community.repository.CommunityRepository
import com.example.nbe233team9.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val communityRepository: CommunityRepository,
    private val communityLikeRepository: CommunityLikeRepository,
    private val userRepository: UserRepository,
) {

    fun createComment(userId: Long, postingId: Long, parentId: Long?, commentRequestDTO: CommentRequestDTO): CommentResponseDTO {
        // 게시글 조회
        val community = communityRepository.findById(postingId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_POST) }

        // 유저 조회
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_USER) }

        // 부모 댓글 조회 (대댓글인 경우)
        val parentComment = parentId?.let {
            commentRepository.findById(it)
                .orElseThrow { CustomException(ResultCode.NOT_EXISTS_COMMENT) }
        }

        // 댓글 생성
        val comment = Comment(
            community = community,
            user = user,
            content = commentRequestDTO.content,
            parent = parentComment
        )

        commentRepository.save(comment)

        // 해당 게시글 댓글 수 증가
        community.updateCommentCount(community.commentCount + 1)
        communityRepository.save(community)

        return CommentResponseDTO.fromEntity(comment)
    }

    fun updateComment(commentId: Long, commentRequestDTO: CommentRequestDTO): CommentResponseDTO {
        // 댓글 조회
        val comment = commentRepository.findById(commentId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_COMMENT) }

        // 댓글 수정
        comment.updateContent(commentRequestDTO)
        commentRepository.save(comment)

        return CommentResponseDTO.fromEntity(comment)
    }

    fun deleteComment(commentId: Long) {
        // 댓글 조회
        val comment = commentRepository.findById(commentId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_COMMENT) }

        val community = comment.community

        // 댓글 삭제
        commentRepository.deleteById(commentId)

        // 해당 게시글 댓글 수 감소
        community.updateCommentCount(community.commentCount - 1)
        communityRepository.save(community)
    }

    fun createLike(userId: Long, postId: Long): LikeResponseDTO {
        // 게시글 조회
        val community = communityRepository.findById(postId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_POST) }

        // 유저 조회
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_USER) }

        // 이미 좋아요를 누른 상태인지 확인
        if (communityLikeRepository.existsByCommunityIdAndUserId(community.id!!, user.id!!)) {
            throw CustomException(ResultCode.DUPLICATE_LIKE)
        }

        // 좋아요 생성
        val communityLike = CommunityLike(
            community = community,
            user = user
        )

        communityLikeRepository.save(communityLike)

        // 좋아요 개수 증가
        community.updateLikeCount(community.likeCount + 1)
        communityRepository.save(community)

        return LikeResponseDTO(communityLike.id!!, community.id!!, user.id!!)
    }

    fun deleteLike(userId: Long, postId: Long) {
        // 게시글 조회
        val community = communityRepository.findById(postId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_POST) }

        // 유저 조회
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_USER) }

        // 좋아요 조회
        val communityLike = communityLikeRepository.findByCommunityIdAndUserId(community.id!!, user.id!!)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_LIKE) }

        // 좋아요 삭제
        communityLikeRepository.delete(communityLike)

        // 좋아요 개수 감소
        community.updateLikeCount(community.likeCount - 1)
        communityRepository.save(community)
    }

    fun getReplies(userId: Long, parentId: Long): List<CommentResponseDTO> {
        return commentRepository.findByParentId(parentId).map { comment ->
            CommentResponseDTO.fromEntity(comment).copy(
                canEdit = comment.user.id == userId
            )
        }
    }
}