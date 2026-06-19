# ---- Build stage: 소스를 받아서 Gradle Wrapper로 빌드 ----
FROM amazoncorretto:17 AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew clean build -x test

# ---- Runtime stage: 빌드된 jar만 실행 ----
FROM amazoncorretto:17
WORKDIR /app

# 프로젝트 정보 (실행할 jar 이름 추론에 사용)
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
# JVM 옵션 (기본값 빈 문자열)
ENV JVM_OPTS=""

# 빌드 스테이지에서 만든 jar만 복사 (이름은 위 환경변수로 결정)
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ./

EXPOSE 80
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar"]