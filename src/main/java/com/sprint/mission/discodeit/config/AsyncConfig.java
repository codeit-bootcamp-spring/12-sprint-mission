package com.sprint.mission.discodeit.config;

import java.util.List;
import java.util.Map;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableAsync
@Configuration
public class AsyncConfig {

  @Bean(name = "eventTaskExecutor")
  public TaskExecutor eventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("event-task-");

    executor.setTaskDecorator(new CompositeTaskDecorator(
            List.of(
                mdcTaskDecorator(),
                securityContextTaskDecorator()
            )
        )
    );

    executor.initialize();

    return executor;
  }

  public TaskDecorator mdcTaskDecorator() {
    return task -> {
      Map<String, String> contextMap = MDC.getCopyOfContextMap();

      return () -> {
        try {
          if (contextMap != null) {
            MDC.setContextMap(contextMap);
          }

          task.run();
        } finally {
          MDC.clear();
        }
      };
    };
  }

  public TaskDecorator securityContextTaskDecorator() {
    return task -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      return () -> {
        try {
          if (authentication != null) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
          }

          task.run();
        } finally {
          SecurityContextHolder.clearContext();
        }
      };
    };
  }
}