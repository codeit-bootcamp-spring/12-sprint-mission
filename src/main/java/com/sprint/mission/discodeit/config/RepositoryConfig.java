package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DiscodeitRepositoryProperties.class)
public class RepositoryConfig {

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "jcf",
            matchIfMissing = true
    )
    public UserRepository jcfUserRepository() {
        return new JCFUserRepository();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "file"
    )
    public UserRepository fileUserRepository(DiscodeitRepositoryProperties properties) {
        return new FileUserRepository(properties.getFileDirectory());
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "jcf",
            matchIfMissing = true
    )
    public ChannelRepository jcfChannelRepository() {
        return new JCFChannelRepository();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "file"
    )
    public ChannelRepository fileChannelRepository(DiscodeitRepositoryProperties properties) {
        return new FileChannelRepository(properties.getFileDirectory());
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "jcf",
            matchIfMissing = true
    )
    public MessageRepository jcfMessageRepository() {
        return new JCFMessageRepository();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "file"
    )
    public MessageRepository fileMessageRepository(DiscodeitRepositoryProperties properties) {
        return new FileMessageRepository(properties.getFileDirectory());
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "jcf",
            matchIfMissing = true
    )
    public ReadStatusRepository jcfReadStatusRepository() {
        return new JCFReadStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "file"
    )
    public ReadStatusRepository fileReadStatusRepository(DiscodeitRepositoryProperties properties) {
        return new FileReadStatusRepository(properties.getFileDirectory());
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "jcf",
            matchIfMissing = true
    )
    public UserStatusRepository jcfUserStatusRepository() {
        return new JCFUserStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "file"
    )
    public UserStatusRepository fileUserStatusRepository(DiscodeitRepositoryProperties properties) {
        return new FileUserStatusRepository(properties.getFileDirectory());
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "jcf",
            matchIfMissing = true
    )
    public BinaryContentRepository jcfBinaryContentRepository() {
        return new JCFBinaryContentRepository();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "file"
    )
    public BinaryContentRepository fileBinaryContentRepository(DiscodeitRepositoryProperties properties) {
        return new FileBinaryContentRepository(properties.getFileDirectory());
    }

}
