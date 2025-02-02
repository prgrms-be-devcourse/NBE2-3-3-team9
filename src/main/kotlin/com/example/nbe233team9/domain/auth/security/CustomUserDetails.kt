package com.example.nbe233team9.domain.auth.security



import com.example.nbe233team9.domain.user.model.User
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class CustomUserDetails @JsonCreator constructor(
    @JsonProperty("user") private val user: User?,
    @JsonProperty("userId") private val userId: Long
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val role = "ROLE_${user?.role?.name ?: "USER"}"
        return listOf(GrantedAuthority { role })
    }

    fun getUserId(): Long {
        return userId // ID를 반환
    }

    override fun getPassword(): String? {
        return null // 비밀번호를 저장하지 않으므로 null 반환
    }

    override fun getUsername(): String {
        return userId.toString() // User ID를 문자열로 반환
    }

    override fun isAccountNonExpired(): Boolean {
        return true // 계정이 만료되지 않았다고 가정
    }

    override fun isAccountNonLocked(): Boolean {
        return true // 계정이 잠기지 않았다고 가정
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true // 인증 정보가 만료되지 않았다고 가정
    }

    override fun isEnabled(): Boolean {
        return true // 계정이 활성화되어 있다고 가정
    }
}
