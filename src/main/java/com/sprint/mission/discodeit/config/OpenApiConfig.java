package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPIBasic() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Discodeit REST API 문서")
                        .version("1.0.0")
                        .description("Discodeit 프로젝트의 REST API 문서입니다.")
                );
    }
}
