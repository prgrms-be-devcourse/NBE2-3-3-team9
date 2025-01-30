package com.example.nbe233team9.domain.community.model

import com.example.nbe233team9.common.entities.CommonEntity
import com.example.nbe233team9.domain.user.model.User
import jakarta.persistence.*
import lombok.NoArgsConstructor

@Entity
class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "community_id")
    val community: Community,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    val user: User,

    var content: String,

    @ManyToOne
    @JoinColumn(name = "parent_id")
    val parent: Comment? = null
) : CommonEntity() {

    fun updateContent(newContent: String) {
        this.content = newContent
    }
}