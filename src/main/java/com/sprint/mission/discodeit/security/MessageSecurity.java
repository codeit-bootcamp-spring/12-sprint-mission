package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("messageSecurity")
@RequiredArgsConstructor
public class MessageSecurity {

    private final MessageRepository messageRepository;

    public boolean isOwner(UUID messageId, Authentication authentication) {
        if(!(authentication.getPrincipal()instanceof DiscodeitUserDetails userDetails)) {
            return false;
        }

        UUID userId = userDetails.getUserResponse().id();

        return messageRepository.existsByIdAndAuthor_Id(messageId, userId);
    }

}
