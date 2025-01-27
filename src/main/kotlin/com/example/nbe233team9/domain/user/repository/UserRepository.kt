package com.example.nbe233team9.domain.user.repository

import com.example.nbe233team9.domain.user.model.Role
import com.example.nbe233team9.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional


@Repository
interface UserRepository :JpaRepository<User, Long> {

    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): Optional<User>

    fun findByRole(role: Role): List<User>
}