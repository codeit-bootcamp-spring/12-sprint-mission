package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

@Service("basicChannelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;

  @Override
  public Channel createPublicChannel(PublicChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());

    if (channelRepository.findAll().stream().anyMatch(c -> c.getName().equals(request.name()))) {
      throw new IllegalArgumentException(
          "Channel with newName " + request.name() + " already exists");
    }

    return channelRepository.save(channel);
  }

  public Channel createPrivateChannel(PrivateChannelCreateRequest request) {
    if (request.participantIds().size() < 2) {
      throw new IllegalArgumentException("Private channel must have at least 2 participantIds");
    }
//
//    if (channelRepository.findAll().stream().anyMatch(c -> c.getName().equals(request.newName()))) {
//      throw new IllegalArgumentException(
//          "Private channel with newName " + request.newName() + " already exists");
//    }

    //잠시 수정
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);

    request.participantIds().forEach(userId -> {
      if (!userRepository.existsById(userId)) {
        throw new NoSuchElementException("User with id " + userId + " not found");
      }
      ReadStatus readStatus = new ReadStatus(userId, channel.getId(), Instant.MIN);
      readStatusRepository.save(readStatus);
    });

    return channelRepository.save(channel);
  }

  @Override
  public ChannelDto find(UUID channelId) {
    Channel ch = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException(
            "Channel with id " + channelId + " not found (channel -> find)"));

    Instant lastMessageTime = messageRepository.findLatestMessageAtChannel(channelId)
        .map(Message::getCreatedAt)
        .orElse(null);

    if (ch.getType() == ChannelType.PRIVATE) {
      List<UUID> userIds = readStatusRepository.findAllByChannelId(channelId).stream()
          .map(ReadStatus::getUserId)
          .toList();

      return new ChannelDto(ch.getId(), ch.getName(), null, ch.getType(), lastMessageTime, userIds);
    }

    return new ChannelDto(ch.getId(), ch.getName(), ch.getDescription(), ch.getType(),
        lastMessageTime, null);
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> publicChannelIds = channelRepository.findAll().stream()
        .filter(c -> c.getType() == ChannelType.PUBLIC)
        .map(Channel::getId)
        .toList();

    List<UUID> privateChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannelId)
        .toList();

    return Stream.concat(publicChannelIds.stream(), privateChannelIds.stream())
        .distinct()
        .map(this::find)
        .toList();
  }

  @Override
  public ChannelDto update(ChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(request.id())
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + request.id() + " not found"));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }

    channel.update(request.newName(), request.newDescription());
    channelRepository.save(channel);

    return find(channel.getId());
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    readStatusRepository.deleteByChannelId(channelId);
    messageRepository.deleteByChannelId(channelId);
    channelRepository.deleteById(channelId);
  }
}
