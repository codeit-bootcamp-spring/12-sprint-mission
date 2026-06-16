package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Primary
@Service
@Transactional
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final UserMapper userMapper;

  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    Channel saved = channelRepository.save(channel);
    log.info("Public channel created: id={}, name={}", saved.getId(), saved.getName());
    return channelMapper.toDto(saved, null, List.of());
  }

  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel saved = channelRepository.save(channel);

    // PRIVATE 채널: 참여자별 ReadStatus 생성
    request.participantIds().stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId)))
        .map(user -> new ReadStatus(user, saved, saved.getCreatedAt()))
        .forEach(readStatusRepository::save);

    log.info("Private channel created: id={}", saved.getId());
    return channelMapper.toDto(saved, null, List.of());
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));
    return buildChannelDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> myChannelIds = readStatusRepository.findAllByUser_Id(userId).stream()
        .map(rs -> rs.getChannel().getId())
        .toList();

    List<Channel> channels = channelRepository.findAll().stream()
        .filter(c -> c.getType() == ChannelType.PUBLIC || myChannelIds.contains(c.getId()))
        .toList();

    List<UUID> channelIds = channels.stream().map(Channel::getId).toList();

    // N+1 해결: 채널 목록의 마지막 메시지 시각을 한 번의 GROUP BY 쿼리로 조회
    Map<UUID, Instant> lastMessageAtMap = messageRepository
        .findLastMessageAtByChannelIds(channelIds).stream()
        .collect(Collectors.toMap(
            row -> (UUID) row[0],
            row -> (Instant) row[1]
        ));

    // N+1 해결: PRIVATE 채널 참여자를 채널 ID 목록으로 한 번에 조회
    Map<UUID, List<UserDto>> participantsMap = readStatusRepository
        .findAllByChannel_IdIn(channelIds).stream()
        .collect(Collectors.groupingBy(
            rs -> rs.getChannel().getId(),
            Collectors.mapping(rs -> userMapper.toDto(rs.getUser()), Collectors.toList())
        ));

    return channels.stream()
        .map(channel -> {
          Instant lastMessageAt = lastMessageAtMap.getOrDefault(channel.getId(), null);
          List<UserDto> participants = channel.getType() == ChannelType.PRIVATE
              ? participantsMap.getOrDefault(channel.getId(), List.of())
              : List.of();
          return channelMapper.toDto(channel, lastMessageAt, participants);
        })
        .toList();
  }

  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelUpdateException(channelId);
    }
    channel.update(request.newName(), request.newDescription());
    log.info("Channel updated: id={}", channelId);
    return buildChannelDto(channelRepository.save(channel));
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));
    channelRepository.delete(channel);
    log.info("Channel deleted: id={}", channelId);
  }

  // 단건 채널 변환: 단건 조회 시에는 개별 쿼리로 필요한 데이터 로드
  private ChannelDto buildChannelDto(Channel channel) {
    Instant lastMessageAt = messageRepository.findAllByChannel_Id(channel.getId()).stream()
        .map(m -> m.getCreatedAt())
        .max(Instant::compareTo)
        .orElse(null);

    List<UserDto> participants = List.of();
    if (channel.getType() == ChannelType.PRIVATE) {
      participants = readStatusRepository.findAllByChannel_Id(channel.getId()).stream()
          .map(rs -> userMapper.toDto(rs.getUser()))
          .toList();
    }

    return channelMapper.toDto(channel, lastMessageAt, participants);
  }
}
