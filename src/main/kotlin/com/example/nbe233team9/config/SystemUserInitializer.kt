package com.example.nbe233team9.config

import com.example.nbe233team9.domain.user.model.Role
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import jakarta.annotation.PostConstruct

@Component
class SystemUserInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PostConstruct
    fun initSystemUser() {
        val existingUser = userRepository.findByEmail("system@admin.com")

        if (existingUser.isEmpty) {
            val systemUser = User(
                email = "system@anicare.com",
                name = "SYSTEM",
                password = passwordEncoder.encode("system123"), // 기본 비밀번호
                role = Role.SYSTEM,
                refreshToken = null,
                socialAccessToken = null
            )

            userRepository.save(systemUser)
            println("SYSTEM 유저가 자동 생성되었습니다!")
        } else {
            println("SYSTEM 유저가 이미 존재합니다.")
        }
    }
}