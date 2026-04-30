package com.sprint.mission.discodeit.dto.data.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelDtoMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

    public ChannelDto toDto(Channel channel) {
      Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
          .stream()
          .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
          .map(Message::getCreatedAt)
          .limit(1)
          .findFirst()
          .orElse(Instant.MIN);

      List<UUID> participantIds = new ArrayList<>();
      if (channel.getType().equals(ChannelType.PRIVATE)) {
        readStatusRepository.findAllByChannelId(channel.getId())
            .stream()
            .map(ReadStatus::getUserId)
            .forEach(participantIds::add);
      }

      return new ChannelDto(
          channel.getId(),
          channel.getType(),
          channel.getName(),
          channel.getDescription(),
          participantIds,
          lastMessageAt
      );
    }
  }