package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelPrivateCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelPublicCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelCannotUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public ChannelResponse createPublic(ChannelPublicCreateRequest request) {
    Channel channel = Channel.createPublic(
        request.name(),
        request.description()
    );

    Channel savedChannel = channelRepository.save(channel);
    log.info("공개 채널 생성 완료. channelId={}, name={}",
        savedChannel.getId(),
        savedChannel.getName()
    );

    return ChannelResponse.from(savedChannel, List.of(), Instant.now());
  }

  @Override
  public ChannelResponse createPrivate(ChannelPrivateCreateRequest request) {
    Channel channel = Channel.createPrivate(request.name());
    Channel savedChannel = channelRepository.save(channel);

    List<UserResponse> participants = new ArrayList<>();
    for (UUID userId : request.participantIds()) {
      User user = getUserOrThrow(userId);

      ReadStatus readStatus = new ReadStatus(
          user,
          savedChannel,
          Instant.now()
      );
      readStatusRepository.save(readStatus);
      participants.add(UserResponse.from(user));
    }

    log.info("비공개 채널 생성 완료. channelId={}, name={}, participantCount={}",
        savedChannel.getId(),
        savedChannel.getName(),
        participants.size()
    );
    return ChannelResponse.from(savedChannel, participants, Instant.now());
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    getUserOrThrow(userId);

    List<Channel> channels = channelRepository.findAll();
    List<ChannelResponse> responses = new ArrayList<>();
    for (Channel channel : channels) {
      if (!availableAccessChannel(channel, userId)) {
        continue;
      }

      List<UserResponse> participants = getParticipants(channel);
      Instant lastMessageAt = getLastMessageAt(channel);

      responses.add(ChannelResponse.from(channel, participants, lastMessageAt));
    }

    return responses;
  }

  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public ChannelResponse update(UUID channelId, ChannelUpdateRequest request) {
    Channel channel = getChannelOrThrow(channelId);

    if (channel.getType() == ChannelType.PRIVATE) {
      log.warn("채널 수정 실패 - 비공개 채널은 수정할 수 없음. channelId={}", channelId);
      throw new PrivateChannelCannotUpdateException(channelId);
    }

    channel.changeChannel(request.newName(), request.newDescription());
    Channel updatedChannel = channelRepository.save(channel);

    log.info("채널 수정 완료. channelId={}, name={}",
        updatedChannel.getId(),
        updatedChannel.getName()
    );
    return ChannelResponse.from(updatedChannel, List.of(), getLastMessageAt(channel));
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public void delete(UUID id) {
    getChannelOrThrow(id);

    messageRepository.deleteByChannelId(id);
    readStatusRepository.deleteByChannelId(id);
    channelRepository.deleteById(id);

    log.info("채널 삭제 완료. channelId={}", id);
  }

  private Channel getChannelOrThrow(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("채널 조회 실패 - 채널을 찾을 수 없음. channelId={}", channelId);
          return new ChannelNotFoundException(channelId);
        });
  }

  private User getUserOrThrow(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자 조회 실패 - 사용자를 찾을 수 없음. userId={}", userId);
          return new UserNotFoundException(userId);
        });
  }

  private boolean availableAccessChannel(Channel channel, UUID userId) {
    if (channel.getType() == ChannelType.PUBLIC) {
      return true;
    }

    if (channel.getType() == ChannelType.PRIVATE) {
      return readStatusRepository.findByUserIdAndChannelId(userId, channel.getId())
          .isPresent();
    }

    return false;
  }

  private List<UserResponse> getParticipants(Channel channel) {
    if (channel.getType() == ChannelType.PUBLIC) {
      return List.of();
    }

    return readStatusRepository.findAllByChannelId(channel.getId()).stream()
        .map(ReadStatus::getUser)
        .map(UserResponse::from)
        .toList();
  }

  private Instant getLastMessageAt(Channel channel) {
    return messageRepository.findTopByChannelOrderByCreatedAtDesc(channel)
        .map(Message::getCreatedAt)
        .orElse(null);
  }

}
