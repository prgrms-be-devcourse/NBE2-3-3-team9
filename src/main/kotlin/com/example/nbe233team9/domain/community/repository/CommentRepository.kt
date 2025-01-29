package com.example.nbe233team9.domain.community.repository

import com.example.nbe233team9.domain.community.model.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {

    fun findByCommunityIdAndParentIsNull(communityId: Long): List<Comment>

    fun findByParentId(parentId: Long): List<Comment>
}