package com.sprint.mission.discodeit.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

// 캐시 스펙 자체는 application.yaml의 spring.cache.* 에 둔다
@EnableCaching
@Configuration
public class CacheConfig {

  public static final String CHANNELS = "channels";
  public static final String NOTIFICATIONS = "notifications";
  public static final String USERS = "users";
}
