package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @EnableJpaAuditing을 별도 @Configuration 클래스에 분리
// → @WebMvcTest 슬라이스 테스트에서 JPA 없이 로드할 때 이 클래스를 excludeFilters로 제외 가능
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

}
