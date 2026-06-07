package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// uses: author(User→UserDto), attachments(BinaryContent→BinaryContentDto) 각각 위임
@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

  // channelId는 중첩 객체에서 추출: message.channel.id
  @Mapping(target = "channelId", source = "channel.id")
  MessageDto toDto(Message message);
}
