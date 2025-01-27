package com.example.nbe233team9.common.config

import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
 class MapperConfig {

    @Bean
     fun modelMapper(): ModelMapper {
        return ModelMapper()
    }
}