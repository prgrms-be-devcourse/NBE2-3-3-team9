package com.example.nbe233team9.domain.auth.config

import com.example.nbe233team9.domain.auth.security.JwtAuthenticationFilter
import com.example.nbe233team9.domain.auth.security.JwtTokenProvider
import com.example.nbe233team9.domain.user.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .cors().and()
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/api-ui.html",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/**",
                        "/api-docs/**"
                    ).permitAll()
                    .requestMatchers("/api/auth/kakao/**").permitAll()
                    .requestMatchers("/api/**").permitAll()
                    .requestMatchers("/back/**").permitAll()
                    .requestMatchers(
                        "/chat-socket/**",
                        "/topic/**",
                        "/app/**",
                        "/ws/**",
                        "/api/chat/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint(CustomAuthenticationEntryPoint())
                    .accessDeniedHandler(CAccessDeniedHandler())
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider, userRepository),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .headers { headers ->
                headers.frameOptions().disable()
            }
        return http.build()
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:63342",
                        "http://localhost:8080",
                        "127.0.0.1:6379"
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
            }
        }
    }
}
