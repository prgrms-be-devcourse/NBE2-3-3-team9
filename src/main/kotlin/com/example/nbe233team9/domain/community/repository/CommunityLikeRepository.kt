package com.example.nbe233team9.domain.community.repository

import com.example.nbe233team9.domain.community.model.CommunityLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommunityLikeRepository : JpaRepository<CommunityLike, Long> {

    fun existsByCommunityIdAndUserId(communityId: Long, userId: Long): Boolean

    fun findByCommunityIdAndUserId(communityId: Long, userId: Long): Optional<CommunityLike>
}