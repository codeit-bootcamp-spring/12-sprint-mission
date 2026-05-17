package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

    @Mapping(target = "participants", source = "participants")
    @Mapping(target = "lastMessageAt", source = "lastMessageAt")
    ChannelDto toDto(
            Channel channel,
            List<UserDto> participants,
            Instant lastMessageAt
    );
}
