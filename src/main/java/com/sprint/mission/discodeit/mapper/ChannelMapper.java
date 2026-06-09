package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// ChannelMapper는 MapStruct로 변환하기 어려운 계산 로직이 있어 수동 유지
// (lastMessageAt, participants를 서비스에서 배치 조회한 뒤 주입받는 구조)
@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final UserMapper userMapper;

  // 단건 채널 변환: 서비스에서 이미 계산된 lastMessageAt, participants를 받아 조립
  public ChannelDto toDto(Channel channel, Instant lastMessageAt, List<UserDto> participants) {
    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt
    );
  }
}
