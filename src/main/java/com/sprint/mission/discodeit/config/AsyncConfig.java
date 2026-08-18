package com.sprint.mission.discodeit.config;

import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 비동기 작업용 스레드풀 설정.
 *
 * <p>성격이 다른 두 작업을 별도 풀로 격리한다(bulkhead).
 * 파일 업로드는 건당 수 초가 걸리는 blocking I/O라, 같은 풀을 쓰면 업로드가 풀을 점유하는 동안
 * 수 ms면 끝나는 알림 생성이 큐 뒤에서 통째로 밀린다(head-of-line blocking).
 *
 * <p>두 풀 모두 큐를 유한하게 두고 CallerRunsPolicy를 쓴다. 큐가 가득 차면 제출한 스레드가
 * 직접 작업을 실행하므로 유입 속도가 자연히 느려지고(backpressure), 작업이 버려지지 않는다.
 * 큐가 무한하면 maxPoolSize에는 영원히 도달하지 않고 메모리에 byte[]만 쌓인다.
 */
@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

  @Bean
  public TaskDecorator taskDecorator() {
    return new MdcSecurityTaskDecorator();
  }

  /** 알림 생성처럼 짧고 빈번한 이벤트 처리용. */
  @Bean("eventTaskExecutor")
  public ThreadPoolTaskExecutor eventTaskExecutor(TaskDecorator taskDecorator) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("event-");
    executor.setTaskDecorator(taskDecorator);
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    // 종료 시 진행 중인 알림 생성을 마저 끝내고 내려간다
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    executor.initialize();
    return executor;
  }

  /** 바이너리 데이터 저장용. 건당 오래 걸리므로 풀을 따로 쓴다. */
  @Bean("fileUploadTaskExecutor")
  public ThreadPoolTaskExecutor fileUploadTaskExecutor(TaskDecorator taskDecorator) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // I/O 대기가 대부분이라 CPU 코어 수보다 크게 잡아도 된다
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("file-upload-");
    executor.setTaskDecorator(taskDecorator);
    // 업로드를 버리면 메시지에 빈 첨부가 남는다. 느려지더라도 유실은 막는다.
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(60);
    executor.initialize();
    return executor;
  }

  /**
   * 반환 타입이 void인 {@code @Async} 메소드에서 예외가 터지면 호출자에게 전파될 곳이 없어
   * 기본 설정에서는 조용히 사라진다. 최소한 로그로는 남긴다.
   */
  @Override
  public org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
      getAsyncUncaughtExceptionHandler() {
    return (ex, method, params) ->
        log.error("비동기 작업에서 처리되지 않은 예외: method={}", method.getName(), ex);
  }
}
