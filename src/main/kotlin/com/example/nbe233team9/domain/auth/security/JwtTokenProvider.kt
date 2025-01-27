package com.example.nbe233team9.domain.auth.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider {

    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    private val accessTokenExpire: Long = 3600000 // 1시간
    private val refreshTokenExpire: Long = 604800000 // 7일

    // AccessToken 생성
    fun createToken(id: Long): String {
        val claims: Claims = Jwts.claims().setSubject(id.toString())
        val now = Date()
        val validity = Date(now.time + accessTokenExpire)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    // RefreshToken 생성
    fun createRefreshToken(id: Long): String {
        val claims: Claims = Jwts.claims().setSubject(id.toString())
        val now = Date()
        val validity = Date(now.time + refreshTokenExpire)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    // JWT 검증
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    // JWT에서 ID 추출
    fun getId(token: String): Long {
        val id = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
        return id.toLong()
    }
}
