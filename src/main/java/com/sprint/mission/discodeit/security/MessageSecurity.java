package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSecurity {

    private final MessageRepository messageRepository;

    public boolean isAuthor(UUID messageId, UUID userId) {
        return messageRepository.findById(messageId)
                .map(message -> message.getAuthor().getId().equals(userId))
                .orElse(false);
    }
}