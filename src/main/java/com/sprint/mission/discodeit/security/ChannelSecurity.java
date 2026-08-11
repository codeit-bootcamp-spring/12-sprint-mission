package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.channel.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("channelSecurity")
@RequiredArgsConstructor
public class ChannelSecurity {

    private final ChannelRepository channelRepository;

    public boolean isPrivate(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(channel -> channel.getType() == ChannelType.PRIVATE)
                .orElse(false);
    }
}