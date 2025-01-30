package com.example.nbe233team9.domain.user.service

import com.example.nbe233team9.domain.user.model.Role
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.repository.UserRepository
import org.springframework.stereotype.Service


@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun saveUser(nickname: String, email: String, profileImg: String, role: Role): User {
        val existingUser = userRepository.findByEmail(email)
        return existingUser.orElseGet {
            val user = User(
                name = nickname,
                email = email,
                profileImg = profileImg,
                role = role
            )
            userRepository.save(user)
        }
    }

    fun updateUser(user: User) {
        userRepository.save(user)
    }



}