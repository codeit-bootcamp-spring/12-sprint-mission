package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

// 캐시 스펙 자체는 application.yaml의 spring.cache.* 에 둔다
@EnableCaching
@Configuration
public class CacheConfig {

  public static final String CHANNELS = "channels";
  public static final String NOTIFICATIONS = "notifications";
  public static final String USERS = "users";

  /**
   * Redis 캐시 직렬화 설정.
   *
   * <p>로컬 캐시는 객체 참조를 그대로 들고 있지만 Redis는 바이트로 직렬화해 저장하므로,
   * 무엇을 어떻게 쓰고 읽을지 명시해야 한다. 기본 JDK 직렬화는 사람이 읽을 수 없고 클래스가
   * 조금만 바뀌어도 깨지므로 JSON을 쓴다.
   *
   * <p>JSON만으로는 {@code List<UserDto>}의 원소 타입 같은 제네릭 정보가 지워져 복원할 수 없다.
   * defaultTyping은 타입 정보를 값에 함께 적어 이를 해결한다.
   *
   * <p><b>주의</b>: {@code LaissezFaireSubTypeValidator} + {@code DefaultTyping.EVERYTHING}은
   * JSON에 적힌 클래스 이름을 그대로 믿고 인스턴스화한다. Redis에 임의의 값을 넣을 수 있는
   * 공격자가 있다면 원격 코드 실행으로 이어질 수 있는, 널리 알려진 위험한 조합이다.
   * 이 캐시에는 애플리케이션이 쓴 값만 들어가고 Redis가 외부에 노출되지 않는다는 전제에서만
   * 안전하다. 운영에서는 캐시별로 타입을 고정한 직렬화기를 쓰고 Redis 접근을 망 수준에서 막아야 한다.
   */
  @Bean
  @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
  public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
    // 애플리케이션 전역 ObjectMapper를 오염시키지 않도록 복사본에만 설정을 건다
    ObjectMapper redisObjectMapper = objectMapper.copy();
    redisObjectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        DefaultTyping.EVERYTHING,
        As.PROPERTY
    );

    return RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(redisObjectMapper)
            )
        )
        // 다른 용도로 같은 Redis를 쓰더라도 키가 섞이지 않게 한다
        .prefixCacheNameWith("discodeit:")
        .entryTtl(Duration.ofSeconds(600))
        // null을 캐싱하면 "조회했지만 없음"과 "아직 안 읽음"을 구분할 수 없다
        .disableCachingNullValues();
  }
}
