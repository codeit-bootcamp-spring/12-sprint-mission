package com.sprint.mission.discodeit.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @DataJpaTest 슬라이스 테스트에서 JPA Auditing 활성화용 공통 TestConfiguration
// 각 테스트 클래스에서 @Import(TestJpaAuditingConfig.class)로 사용
@TestConfiguration
@EnableJpaAuditing
public class TestJpaAuditingConfig {

}
