package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {


    @Bean
    public UserRepository userRepository(RepositoryProperties p) {
        return "file".equalsIgnoreCase(p.getType())
                ? new FileUserRepository(p.getFileDirectory())
                : new JCFUserRepository();
    }

    @Bean
    public ChannelRepository channelRepository(RepositoryProperties p) {
        return "file".equalsIgnoreCase(p.getType())
                ? new FileChannelRepository(p.getFileDirectory())
                : new JCFChannelRepository();
    }

    @Bean
    public MessageRepository messageRepository(RepositoryProperties p) {
        return "file".equalsIgnoreCase(p.getType())
                ? new FileMessageRepository(p.getFileDirectory())
                : new JCFMessageRepository();
    }

    @Bean
    public ReadStatusRepository readStatusRepository(RepositoryProperties p) {
        return "file".equalsIgnoreCase(p.getType())
                ? new FileReadStatusRepository(p.getFileDirectory())
                : new JCFReadStatusRepository();
    }

    @Bean
    public UserStatusRepository userStatusRepository(RepositoryProperties p) {
        return "file".equalsIgnoreCase(p.getType())
                ? new FileUserStatusRepository(p.getFileDirectory())
                : new JCFUserStatusRepository();
    }

    @Bean
    public BinaryContentRepository binaryContentRepository(RepositoryProperties p) {
        return "file".equalsIgnoreCase(p.getType())
                ? new FileBinaryContentRepository(p.getFileDirectory())
                : new JCFBinaryContentRepository();
    }
}