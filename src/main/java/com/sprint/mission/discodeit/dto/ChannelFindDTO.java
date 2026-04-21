package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ChannelFindDTO {
    private ChannelType type;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant recentMessageAt;
    private List<UUID> userIds;

    public static ChannelFindDTO from (Channel channel) {
        ChannelFindDTO dto = new ChannelFindDTO();
        dto.setType(channel.getType());
        dto.setName(channel.getName());
        dto.setDescription(channel.getDescription());
        dto.setCreatedAt(channel.getCreatedAt());
        dto.setUpdatedAt(channel.getUpdatedAt());
        dto.setRecentMessageAt(null);
        dto.setUserIds(null);
        return dto;
    }
}
