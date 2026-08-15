package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager(
        "channelsByUser",
        "notificationsByUser",
        "users"
    );

    cacheManager.setCaffeine(
        Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(600, TimeUnit.SECONDS)
            .recordStats()
    );

    return cacheManager;
  }
}
