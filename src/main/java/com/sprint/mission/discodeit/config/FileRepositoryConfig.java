package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileRepositoryConfig {

    @Value("${discodeit.repository.file-directory}")
    private String filePath;

    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository(filePath);
    }
    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository(filePath);
    }
    @Bean
    public ChannelRepository channelRepository() {
        return new FileChannelRepository(filePath);
    }
    @Bean
    public UserStatusRepository userStatusRepository() {
        return new FileUserStatusRepository(filePath);
    }
    @Bean
    public ReadStatusRepository readStatusRepository() {
        return new FileReadStatusRepository(filePath);
    }
    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new FileBinaryContentRepository(filePath);
    }
}
