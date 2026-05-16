package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReadStatusMapper {

  private final UserMapper userMapper;
  private final ChannelMapper channelMapper;

  public ReadStatusDto toDto(ReadStatus readStatus) {

    return new ReadStatusDto(
        readStatus.getId(),
        readStatus.getCreatedAt(),
        readStatus.getUpdatedAt(),
        userMapper.toDto(readStatus.getUser()),
        channelMapper.toDto(readStatus.getChannel()),
        readStatus.getLastReadAt()
    );
  }
}
