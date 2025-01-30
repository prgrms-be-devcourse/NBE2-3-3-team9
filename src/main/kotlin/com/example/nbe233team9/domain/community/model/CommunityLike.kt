package com.example.nbe233team9.domain.community.model

import com.example.nbe233team9.common.entities.CommonEntity
import com.example.nbe233team9.domain.user.model.User
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class CommunityLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "community_id")
    val community: Community,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User
) : CommonEntity()