package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.user.User;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "channel", source = "channel")
    @Mapping(target = "lastReadAt", source = "request.lastReadAt")
    ReadStatus toEntity(
            ReadStatusCreateRequest request,
            User user,
            Channel channel
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "channel", source = "channel")
    @Mapping(target = "lastReadAt", source = "lastReadAt")
    ReadStatus toEntity(
            User user,
            Channel channel,
            Instant lastReadAt
    );

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "channelId", source = "channel.id")
    @Mapping(target = "lastReadAt", source = "lastReadAt")
    ReadStatusResponse toResponse(ReadStatus readStatus);
}