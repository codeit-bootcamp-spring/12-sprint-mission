# 빌드 스테이지
FROM amazoncorretto:17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 정보 환경변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

# Gradle Wrapper 파일 복사
COPY gradle ./gradle
COPY gradlew ./gradlew

# Gradle 설정 파일 복사
COPY build.gradle settings.gradle ./

# 의존성 다운로드
RUN ./gradlew dependencies

# 소스 코드 복사
COPY src ./src

# gradle wrapper를 사용하여 빌드
RUN ./gradlew build -x test

# 런타임 스테이지
FROM amazoncorretto:17-alpine3.21

# 작업 디렉토리 설정
WORKDIR /app

# 환경변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 빌드 스테이지에서 jar 파일만 복사
COPY --from=builder /app/build/libs/*.jar /app/

# 80 포트 노출
EXPOSE 80

# jar 파일 실행
ENTRYPOINT ["sh","-c","java $JVM_OPTS -jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]
