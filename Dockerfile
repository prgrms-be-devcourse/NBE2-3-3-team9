FROM gradle:7.6-jdk17 as builder
WORKDIR /app

# Gradle 관련 파일 복사
COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY settings.gradle.kts /app/settings.gradle.kts
COPY build.gradle.kts /app/build.gradle.kts


# Gradle 캐시 활용해 의존성 설치
RUN ./gradlew dependencies --no-daemon

# 전체 소스 복사
COPY . .

# 빌드 실행
RUN ./gradlew build -x test --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
