package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelAccessDeniedException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  //
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final RoleHierarchy roleHierarchy;

  private boolean isChannelManager() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    for (GrantedAuthority authority : roleHierarchy.getReachableGrantedAuthorities(
        auth.getAuthorities())) {
      if (authority.getAuthority().equals("ROLE_CHANNEL_MANAGER")) {
        return true;
      }
    }
    return false;
  }

  private UUID currentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      return userDetails.getUserDto().id();
    }
    return null;
  }

  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  public ChannelDto create(PublicChannelCreateRequest request) {
    log.debug("PublicChannel 생성 시작: request={}", request);
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    log.info("PublicChannel 생성 완료: name={}", name);
    return channelMapper.toDto(channelRepository.save(channel));
  }

  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.debug("PrivateChannel 생성 시작: request={}", request);
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);
    List<User> users = userRepository.findAllById(request.participantIds());
    readStatusRepository.saveAll(users.stream()
        .map(user -> new ReadStatus(user, createdChannel, Instant.now()))
        .toList());
    log.info("PrivateChannel 생성 완료: channelId={}, participantIds={}", createdChannel.getId(),
        request.participantIds());
    return channelMapper.toDto(createdChannel);
  }

  @Override
  public ChannelDto find(UUID channelId) {
    log.debug("Channel 조회 시작: channelId={}", channelId);
    ChannelDto channelDto = channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(
            () -> {
              log.warn("Channel 조회 실패(채널 없음): channelId={}", channelId);
              return ChannelNotFoundException.withId(channelId);
            }
        );
    log.info("Channel 조회 성공: channelId={}, name={}", channelId, channelDto.name());
    return channelDto;
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    log.debug("Channel 목록 조회 시작: userId={}", userId);
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(r -> r.getChannel().getId())
        .toList();
    List<ChannelDto> channelDtos = channelRepository.findAllPublicOrId(mySubscribedChannelIds)
        .stream()
        .map(channelMapper::toDto)
        .toList();
    log.info("Channel 목록 조회 성공: userId={}, channelCount={}", userId, channelDtos.size());
    return channelDtos;
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.debug("Channel 업데이트 시작: channelId={}, request={}", channelId, request);
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> {
              log.warn("Channel 업데이트 실패(채널 없음): channelId={}", channelId);
              return ChannelNotFoundException.withId(channelId);
            });
    if (channel.getType() == ChannelType.PUBLIC) {
      if (!isChannelManager()) {
        throw ChannelAccessDeniedException.withChannelIdAndUserId(channelId, currentUserId());
      }
    } else if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("Channel 업데이트 실패(비공개 채널): channelId={}", channelId);
      throw PrivateChannelUpdateException.withChannelId(channelId);
    }

    channel.update(newName, newDescription);
    log.info("Channel 업데이트 완료: channelId={}, newName={}", channelId, newName);
    return channelMapper.toDto(channelRepository.save(channel));
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    log.debug("Channel 삭제 시작: channelId={}", channelId);
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> {
              log.warn("Channel 삭제 실패(채널 없음): channelId={}", channelId);
              return ChannelNotFoundException.withId(channelId);
            }
        );
    if (channel.getType() == ChannelType.PUBLIC) {
      if (!isChannelManager()) {
        throw ChannelAccessDeniedException.withChannelIdAndUserId(channelId, currentUserId());
      }
    }
    messageRepository.deleteAllByChannelId(channel.getId());
    readStatusRepository.deleteAllByChannelId(channel.getId());

    channelRepository.deleteById(channelId);
    log.info("Channel 삭제 완료: channelId={}", channelId);
  }

}
