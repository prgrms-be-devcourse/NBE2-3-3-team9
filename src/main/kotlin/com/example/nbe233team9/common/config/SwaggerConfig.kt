package com.example.nbe233team9.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
 class SwaggerConfig {
    /**
     * OpenAPI Bean을 생성하여 애플리케이션의 OpenAPI 문서 구성을 제공합니다.
     *
     * @return OpenAPI OpenAPI 설정 객체
     */
    private fun info(): Info {
        return Info()
            .title("Anicare ")
            .version("1.0")
            .description("API 명세서");
    }
    @Bean
     fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .components(
                Components().addSecuritySchemes(
                    "access-token",
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            )
            .addSecurityItem(SecurityRequirement().addList("access-token"))
            .info(info())
    }
}