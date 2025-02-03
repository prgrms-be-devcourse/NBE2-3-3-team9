package com.example.nbe233team9.domain.schedule.task

import com.example.nbe233team9.domain.schedule.repository.SingleScheduleRepository
import com.example.nbe233team9.domain.schedule.service.MessageService
import com.example.nbe233team9.domain.schedule.service.RedisService
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.awt.print.Pageable
import java.time.LocalDateTime


@Component
class SchedulerTask(
    private val messageService: MessageService,
    private val singleScheduleRepository: SingleScheduleRepository,
    private val redisService: RedisService
) {

    @Scheduled(fixedRate = 10000) // 5분마다 실행
    fun requestMessage() {
        var page = 0
        val pageSize = 50
        do {
            // 일정 페이징 조회
            val schedulePage = singleScheduleRepository.findSchedulesWithinNextTenMinutes(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                PageRequest.of(page, pageSize)
            )

            val scheduleIds = schedulePage.content
                .filter { schedule ->
                    val redisKey = "user.${schedule.user.id}.access_token"
                    redisService.getValues(redisKey) != null
                }
                .onEach { schedule ->
                    val redisKey = "user.${schedule.user.id}.access_token"
                    val accessToken = redisService.getValues(redisKey)
                    if (accessToken != null) {
                        messageService.requestMessage(accessToken, schedule)
                    }
                }
                .map { it.id }

            // Batch Update 실행
            if (scheduleIds.isNotEmpty()) {
                singleScheduleRepository.updateNotificationTime(scheduleIds, LocalDateTime.now())
            }

            page++
        } while (schedulePage.hasNext())  // 다음 페이지가 있으면 반복
    }
}

