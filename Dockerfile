#FROM amazoncorretto:17
#
#WORKDIR /app
#
#COPY gradle ./gradle
#COPY gradlew ./gradlew
#
#COPY build.gradle settings.gradle ./
#
#COPY src ./src
#
#RUN ./gradlew build -x test
#
#ENV PROJECT_NAME=discodeit
#ENV PROJECT_VERSION=1.2-M8
#ENV JVM_OPTS=""
#
#EXPOSE 80
#
#ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]

# 1단계: 빌드
FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./gradlew

RUN chmod +x ./gradlew

COPY build.gradle settings.gradle ./

RUN ./gradlew dependencies

COPY src ./src
RUN ./gradlew build -x test

# 2단계: 실행
FROM amazoncorretto:17-al2023-headless

WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]