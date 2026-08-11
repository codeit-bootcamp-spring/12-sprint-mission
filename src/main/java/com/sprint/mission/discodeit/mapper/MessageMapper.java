package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, BinaryContentMapper.class}
)
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "content", source = "request.content")
    @Mapping(target = "channel", source = "channel")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "attachments", source = "attachments")
    Message toEntity(MessageCreateRequest request, Channel channel, User author, List<BinaryContent> attachments);

    @Mapping(target = "id", source = "message.id")
    @Mapping(target = "createdAt", source = "message.createdAt")
    @Mapping(target = "updatedAt", source = "message.updatedAt")
    @Mapping(target = "content", source = "message.content")
    @Mapping(target = "channelId", source = "message.channel.id")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "attachments", source = "message.attachments")
    MessageResponse toResponse(Message message, UserResponse author);
}