package com.example.nbe233team9.domain.auth.client



import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class KakaoClient(
    private val restTemplate: RestTemplate = RestTemplate()
) {

    @Value("\${KAKAO_TOKEN_URI}")
    private lateinit var kakaoTokenUri: String

    @Value("\${KAKAO_USER_INFO_URI}")
    private lateinit var kakaoUserInfoUri: String

    private val objectMapper = ObjectMapper()

    fun getAccessToken(clientId: String, redirectUri: String, code: String): String {
        val headers = HttpHeaders().apply {
            add("Content-Type", "application/x-www-form-urlencoded")
        }

        val body = "grant_type=authorization_code&client_id=$clientId&redirect_uri=$redirectUri&code=$code"
        val request = HttpEntity(body, headers)

        val response: ResponseEntity<String> = restTemplate.exchange(
            kakaoTokenUri, HttpMethod.POST, request, String::class.java
        )

        if (response.statusCode.is2xxSuccessful) {
            return try {
                objectMapper.readTree(response.body).get("access_token").asText()
            } catch (e: Exception) {
                throw RuntimeException("Access Token 응답 파싱에 실패했습니다.", e)
            }
        } else {
            throw RuntimeException("Access Token을 가져오지 못했습니다. 상태 코드: ${response.statusCode}")
        }
    }

    fun getUserInfo(accessToken: String): JsonNode {
        val headers = HttpHeaders().apply {
            add("Authorization", "Bearer $accessToken")
        }

        val request = HttpEntity<Void>(headers)

        val response: ResponseEntity<String> = restTemplate.exchange(
            kakaoUserInfoUri, HttpMethod.GET, request, String::class.java
        )

        if (response.statusCode.is2xxSuccessful) {
            return try {
                objectMapper.readTree(response.body)
            } catch (e: Exception) {
                throw RuntimeException("사용자 정보 응답 파싱에 실패했습니다.", e)
            }
        } else {
            throw RuntimeException("사용자 정보를 가져오지 못했습니다. 상태 코드: ${response.statusCode}")
        }
    }

    fun unlinkKakaoAccount(accessToken: String) {
        val unlinkUrl = "https://kapi.kakao.com/v1/user/unlink"

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
        }

        val request = HttpEntity<Void>(headers)
        val response: ResponseEntity<String> = restTemplate.exchange(unlinkUrl, HttpMethod.POST, request, String::class.java)

        if (!response.statusCode.is2xxSuccessful) {
            throw RuntimeException("Failed to unlink Kakao account: ${response.body}")
        }
    }
}
