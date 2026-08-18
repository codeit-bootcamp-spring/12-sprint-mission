package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

// @Retryable / @Recover는 AOP 프록시로 동작하므로 활성화가 필요하다
@EnableRetry
@Configuration
public class RetryConfig {

}
