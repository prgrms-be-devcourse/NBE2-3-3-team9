package com.example.nbe233team9.domain.schedule.task

import com.example.nbe233team9.domain.schedule.model.SingleSchedule
import com.example.nbe233team9.domain.schedule.repository.SingleScheduleRepository
import com.example.nbe233team9.domain.schedule.service.MessageService
import com.example.nbe233team9.domain.schedule.service.RedisService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime


@Component
class SchedulerTask(
    private val messageService: MessageService,
    private val singleScheduleRepository: SingleScheduleRepository,
    private val redisService: RedisService
) {

    @Scheduled(fixedRate = 60000 * 5)
    fun requestMessage() {
        val lists: List<SingleSchedule> = singleScheduleRepository.findSchedulesWithinNextTenMinutes(
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(10)
        )

        for (schedule in lists) {
            val redisKey = String.format("user.%s.access_token", schedule.user.id)
            val accessToken: String? = redisService.getValues(redisKey)
            if (accessToken != null) {
                messageService.requestMessage(accessToken!!, schedule)
                schedule.notificatedAt = LocalDateTime.now()
                singleScheduleRepository.save(schedule)
            }
        }
    }
}
