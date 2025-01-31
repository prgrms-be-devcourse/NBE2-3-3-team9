package com.example.nbe233team9

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
class Nbe233Team9Application

fun main(args: Array<String>) {
    runApplication<Nbe233Team9Application>(*args)
}
