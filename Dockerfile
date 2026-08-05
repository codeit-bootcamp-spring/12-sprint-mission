FROM --platform=$BUILDPLATFORM amazoncorretto:17 AS builder
#https://docs.docker.com/build/building/multi-platform/

WORKDIR /app

#COPY . .

COPY gradle ./gradle
COPY gradlew ./gradlew

RUN chmod +x ./gradlew

COPY build.gradle settings.gradle ./

RUN ./gradlew dependencies

COPY src ./src
RUN ./gradlew build -x test # 테스트 제외

# 런타임 x                         | 1.4gb
# 런타임 amazoncorretto:17         | 850mb
# 런타임 amazoncorretto:17-al2023  | 830mb
# 런타임 amazoncorretto:17-alpine  | 600mb
# 동작 테스트 결과 런타임 빌드 3개 모두 이상 없음.
FROM amazoncorretto:17

WORKDIR /app

ARG PROJECT_NAME=discodeit
ARG PROJECT_VERSION=1.2-M8

COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar /app/

ENV PROJECT_NAME=${PROJECT_NAME}
ENV PROJECT_VERSION=${PROJECT_VERSION}
ENV JVM_OPTS=""

EXPOSE 80

CMD ["sh", "-c", "java $JVM_OPTS -jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]