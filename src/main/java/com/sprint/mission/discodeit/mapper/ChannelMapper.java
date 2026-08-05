package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

  @Autowired
  protected MessageRepository messageRepository;
  @Autowired
  protected ReadStatusRepository readStatusRepository;
  @Autowired
  protected UserMapper userMapper;

  @Mapping(target = "participants",
          expression = "java(resolveParticipants(channel, sessionRegistry))")
  @Mapping(target = "lastMessageAt",
          expression = "java(resolveLastMessageAt(channel))")
  public abstract ChannelResponse toResponse(Channel channel,
                                             @Context SessionRegistry sessionRegistry);

  protected Instant resolveLastMessageAt(Channel channel) {
    return messageRepository.findLastMessageAtByChannelId(channel.getId())
            .orElse(Instant.MIN);
  }

  protected List<UserResponse> resolveParticipants(Channel channel,
                                                   SessionRegistry sessionRegistry) {
    if (!ChannelType.PRIVATE.equals(channel.getType())) {
      return List.of();
    }
    return readStatusRepository.findAllByChannelIdWithUser(channel.getId())
            .stream()
            .map(ReadStatus::getUser)
            .map(user -> userMapper.toResponse(user, sessionRegistry))
            .toList();
  }
}