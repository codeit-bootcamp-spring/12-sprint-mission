package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new io.swagger.v3.oas.models.info.Info()
            .title("Discodeit API 문서")
            .version("v1.1.0")
            .description("Discodeit 프로젝트의 Swagger API 문서입니다."))
        .servers(java.util.List.of(
            new io.swagger.v3.oas.models.servers.Server()
                .url("http://localhost:8080")   // 도메인 주소 영역
                .description("로컬 서버")
        ))
        .tags(List.of(
            new Tag().name("Channel").description("Channel API"),
            new Tag().name("ReadStatus").description("Message 읽음 상태 API"),
            new Tag().name("Message").description("Message API"),
            new Tag().name("User").description("User API"),
            new Tag().name("BinaryContent").description("첨부 파일 API"),
            new Tag().name("Auth").description("인증 API")
        ));
  }

}
