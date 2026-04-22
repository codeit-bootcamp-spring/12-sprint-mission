package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelDto {
    private final UUID id;
    private final ChannelCategory category;
    private final String name;
    private final String description;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant lastMessageAt;
    private final List<UUID> participantIds;
}
