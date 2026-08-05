package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelMapper channelMapper;
  private final MessageRepository messageRepository;
  private final UserMapper userMapper;
  private final RoleHierarchy roleHierarchy;

  @Override
  @Transactional
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public ChannelDto create(PublicChannelCreateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("request is null.");
    }

    if (!isUniqueName(request.name())) {
      throw ChannelNameAlreadyExistsException.withChannelName(request.name());
    }

    Channel channel = channelMapper.toEntity(request);
    Channel savedChannel = channelRepository.save(channel);

    log.info("public 채널 생성 완료: channelId={}, name={}",
        savedChannel.getId(),
        savedChannel.getName()
    );

    return toDto(savedChannel);
  }

  @Override
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("request is null.");
    }

    Channel channel = Channel.builder()
        .type(ChannelType.PRIVATE)
        .build();
    Channel savedChannel = channelRepository.save(channel);

    Set<UUID> requestedUserIds = new HashSet<>(request.participantIds());

    List<User> participants = userRepository.findAllById(requestedUserIds);

    Set<UUID> foundUserIds = participants.stream()
        .map(User::getId)
        .collect(Collectors.toSet());

    List<UUID> missingUserIds = requestedUserIds.stream()
        .filter(userId -> !foundUserIds.contains(userId))
        .toList();

    if (!missingUserIds.isEmpty()) {
      throw UserNotFoundException.withUserIds(missingUserIds);
    }

    List<ReadStatus> readStatuses = participants.stream()
        .<ReadStatus>map(user -> ReadStatus.builder()
            .user(user)
            .channel(savedChannel)
            .lastReadAt(savedChannel.getCreatedAt())
            .build())
        .toList();

    readStatusRepository.saveAll(readStatuses);

    log.info("private 채널 생성 완료: channelId={}, participantCount={}",
        savedChannel.getId(),
        participants.size()
    );

    return toDto(savedChannel);
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto findById(UUID channelId) {
    if (channelId == null) {
      throw new IllegalArgumentException("channelId is null.");
    }

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelNotFoundException.withChannelId(channelId));
    return toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("userId is null.");
    }

    List<Channel> channels = channelRepository
        .findAllByUserIdAndPublicChannelsWithParticipants(userId);

    List<UUID> channelIds = channels.stream()
        .map(Channel::getId)
        .toList();

    if (channelIds.isEmpty()) {
      return List.of();
    }

    Map<UUID, Instant> lastMessageMap = new HashMap<>();
    messageRepository.findLastMessageAtByChannelIds(channelIds)
        .forEach(obj -> lastMessageMap.put((UUID) obj[0], (Instant) obj[1]));

    return channels.stream()
        .map(channel -> {
          List<UserDto> participants = channel.getReadStatuses().stream()
              .map(ReadStatus::getUser)
              .map(userMapper::toDto)
              .toList();
          return channelMapper.toDto(channel, lastMessageMap.get(channel.getId()), participants);
        })
        .toList();
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, ChannelUpdateRequest request) {
    if (channelId == null) {
      throw new IllegalArgumentException("channelId is null.");
    }
    if (request == null) {
      throw new IllegalArgumentException("channelUpdateRequest is null.");
    }

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelNotFoundException.withChannelId(channelId));

    validateChannelManagerIfPublic(channel);

    if (channel.getType() == ChannelType.PRIVATE) {
      throw PrivateChannelUpdateNotAllowedException.withChannelId(channelId);
    }

    if (!Objects.equals(channel.getName(), request.newName())
        && !isUniqueName(request.newName())) {
      throw ChannelNameAlreadyExistsException.withChannelName(request.newName());
    }

    String beforeName = channel.getName();
    String beforeDescription = channel.getDescription();

    channel.setName(request.newName());
    channel.setDescription(request.newDescription());

    Channel updatedChannel = channelRepository.save(channel);
    ChannelDto channelDto = toDto(updatedChannel);

    log.info("채널 업데이트 완료: channelId={}, nameChanged={}, descriptionChanged={}",
        channelId,
        !Objects.equals(beforeName, request.newName()),
        !Objects.equals(beforeDescription, request.newDescription())
    );

    return channelDto;
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    if (channelId == null) {
      throw new IllegalArgumentException("channelId is null.");
    }

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelNotFoundException.withChannelId(channelId));

    validateChannelManagerIfPublic(channel);

    channelRepository.delete(channel);

    log.info("채널 삭제 완료: channelId={}", channelId);
  }

  private boolean isUniqueName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name is null.");
    }

    return !channelRepository.existsByName(name);
  }

  private ChannelDto toDto(Channel channel) {
    Instant lastMessageAt = messageRepository
        .findTopByChannelIdOrderByCreatedAtDesc(channel.getId())
        .map(Message::getCreatedAt)
        .orElse(null);

    List<UserDto> participants = readStatusRepository
        .findAllByChannelId(channel.getId()).stream()
        .map(ReadStatus::getUser)
        .map(userMapper::toDto)
        .toList();

    return channelMapper.toDto(channel, lastMessageAt, participants);
  }

  private void validateChannelManagerIfPublic(Channel channel) {
    if (channel.getType() != ChannelType.PUBLIC) {
      return;
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    boolean hasChannelManager = authentication != null
        && roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities()).stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_CHANNEL_MANAGER"));

    if (!hasChannelManager) {
      throw new AccessDeniedException("CHANNEL_MANAGER 권한 필요.");
    }
  }
}
