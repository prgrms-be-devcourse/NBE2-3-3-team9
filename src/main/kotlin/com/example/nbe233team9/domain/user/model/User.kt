package com.example.nbe233team9.domain.user.model

import com.example.nbe233team9.common.entities.CommonEntity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0L,

    @Column(unique = true)
    val email: String,

    @Column
    var name: String? = null,

    @Column
    var password: String? = null,

    @Column
    var profileImg: String? = null,

    @Column
    var socialAccessToken: String? = null, // Kakao AccessToken 저장

    @Column(nullable = false)
    var yearsOfExperience: Int = 0,

    @Column
    @JsonIgnore
    var refreshToken: String? = null,


    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER
) : CommonEntity() {

    @PrePersist
    fun prePersist() {
        if (profileImg.isNullOrEmpty()) {
            profileImg = generateGravatarUrl(email)
        }
    }

    private fun generateGravatarUrl(email: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val hash = md.digest(email.trim().lowercase().toByteArray(StandardCharsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }.let {
                "https://www.gravatar.com/avatar/$it?s=200&r=pg&d=mm"
            }
        } catch (e: Exception) {
            throw RuntimeException("Error generating Gravatar URL", e)
        }
    }
}
