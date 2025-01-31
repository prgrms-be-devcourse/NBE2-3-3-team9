package com.example.nbe233team9.domain.schedule.service

import com.example.nbe233team9.domain.schedule.model.SingleSchedule
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.format.DateTimeFormatter

@Service
class MessageService {
    fun requestMessage(accessToken: String, schedule: SingleSchedule) {
        val restTemplate: RestTemplate = RestTemplate()

        val url = "https://kapi.kakao.com/v2/api/talk/memo/default/send"

        val headers = HttpHeaders()
        headers["Authorization"] = "Bearer $accessToken"
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val body: MultiValueMap<String, String> = LinkedMultiValueMap()

        val jsonTemplate = ("{"
                + "\"object_type\":\"text\","
                + "\"text\":\"[알림] 일정이 곧 시작됩니다!\\n" +
                "- 일정: %s\\n" +
                "- 시작 시간: %s\\n" +
                "- 종료 시간: %s\\n\","
                + "\"link\":{"
                + "    \"web_url\":\"http://localhost:3000/\","
                + "    \"mobile_web_url\":\"http://localhost:3000/\""
                + "},"
                + "\"button_title\":\"Anicare Link\""
                + "}")

        val formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분")

        val jsonString = String.format(
            jsonTemplate,
            schedule.name,
            schedule.startDatetime.format(formatter),
            schedule.endDatetime.format(formatter)
        )

        body.add("template_object", jsonString)

        val request = HttpEntity(body, headers)

        val response: ResponseEntity<String> = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            String::class.java
        )

        if (response.getStatusCode().is2xxSuccessful()) {
            println("메시지 전송 성공: " + response.getBody())
        } else {
            println("메시지 전송 실패: " + response.getStatusCode())
        }
    }
}
