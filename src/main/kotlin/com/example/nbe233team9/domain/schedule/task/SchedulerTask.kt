package com.example.nbe233team9.domain.schedule.task

import com.example.nbe233team9.domain.schedule.repository.SingleScheduleBatchUpdateRepository
import com.example.nbe233team9.domain.schedule.repository.SingleScheduleRepository
import com.example.nbe233team9.domain.schedule.service.MessageService
import com.example.nbe233team9.domain.schedule.service.RedisService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime


@Component
class SchedulerTask(
    private val messageService: MessageService,
    private val singleScheduleRepository: SingleScheduleRepository,
    private val redisService: RedisService,
    private val singleScheduleBatchUpdateRepository: SingleScheduleBatchUpdateRepository
) {

    @Scheduled(fixedRate = 60000 * 5) // 5분마다 실행
    fun requestMessage() {
        var page = 0
        val pageSize = 1000
        do {
            val schedulePage = singleScheduleRepository.findSchedulesWithinNextTenMinutes(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                PageRequest.of(page, pageSize, Sort.by("startDatetime").ascending())
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
                singleScheduleBatchUpdateRepository.updateNotificationTime(scheduleIds, LocalDateTime.now())
            }

        } while (schedulePage.hasNext())  // 다음 페이지가 있으면 반복
    }
}

