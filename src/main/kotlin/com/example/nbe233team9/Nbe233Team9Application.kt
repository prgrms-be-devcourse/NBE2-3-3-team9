package com.example.nbe233team9

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class Nbe233Team9Application

fun main(args: Array<String>) {
    runApplication<Nbe233Team9Application>(*args)
}
