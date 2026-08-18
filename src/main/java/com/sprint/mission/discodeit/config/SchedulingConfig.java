package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// JpaAuditingConfig와 같은 이유로 @EnableScheduling을 별도 @Configuration 클래스에 분리
// → 슬라이스 테스트에서 스케줄러를 띄우지 않도록 제외할 수 있다
@Configuration
@EnableScheduling
public class SchedulingConfig {

}
