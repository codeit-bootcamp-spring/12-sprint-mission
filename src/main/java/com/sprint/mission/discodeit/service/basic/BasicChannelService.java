package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.channel.ChannelType;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ReadStatusMapper readStatusMapper;
    private final MessageRepository messageRepository;
    private final MessageService messageService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ChannelResponse createPublicChannel(CreatePublicChannelRequest request) {
        log.info("Public channel create requested. name={}", request.name());

        Channel channel = Channel.createPublic(request.name(), request.description());
        Channel saved = channelRepository.save(channel);

        log.info("Public channel created. channelId={}, name={}", saved.getId(), saved.getName());

        return channelMapper.toResponse(saved, null, List.of());
    }

    @Override
    @Transactional
    public ChannelResponse createPrivateChannel(CreatePrivateChannelRequest request) {
        log.info("Private channel create requested. participantCount={}", request.participantIds().size());

        Channel channel = Channel.createPrivate();
        Channel saved = channelRepository.save(channel);

        Instant now = Instant.now();
        List<UserResponse> participants = new ArrayList<>();

        for (UUID userId : request.participantIds()) {
            User user = getUserOrThrow(userId);

            ReadStatus readStatus = readStatusMapper.toEntity(user, saved, now);
            readStatusRepository.save(readStatus);

            participants.add(userMapper.toResponse(user));
        }

        log.info("Private channel created. channelId={}, participantCount={}", saved.getId(), participants.size());

        return channelMapper.toResponse(saved, null, participants);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelResponse findById(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);

        return toResponse(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        log.debug("Channel findAllByUserId requested. userId={}", userId);

        User user = getUserOrThrow(userId);

        List<UUID> participatedChannelIds = readStatusRepository.findAllByUser_Id(user.getId()).stream()
                .map(readStatus -> readStatus.getChannel().getId())
                .toList();

        List<Channel> channels = channelRepository.findAllByTypeOrIdIn(
                ChannelType.PUBLIC,
                participatedChannelIds
        );

        log.debug("Channel findAllByUserId completed. userId={}, channelCount={}", userId, channels.size());

        return channels.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChannelResponse update(UUID channelId, ChannelUpdateRequest request) {
        log.info("Channel update requested. channelId={}", channelId);

        Channel channel = getChannelOrThrow(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            log.warn("Channel update failed. reason=private_channel_update, channelId={}", channelId);
            throw new PrivateChannelUpdateNotAllowedException(channelId);
        }

        channel.update(request.newName(), request.newDescription());

        log.info("Channel updated. channelId={}", channel.getId());

        return toResponse(channel);
    }

    @Override
    @Transactional
    public void delete(UUID channelId) {
        log.warn("Channel delete requested. channelId={}", channelId);

        Channel channel = getChannelOrThrow(channelId);

        List<Message> messages = messageRepository.findAllByChannel_Id(channelId);
        for (Message message : messages) {
            messageService.delete(message.getId());
        }

        channelRepository.delete(channel);

        log.info("Channel deleted. channelId={}, deletedMessageCount={}", channelId, messages.size());
    }

    private ChannelResponse toResponse(Channel channel) {
        Instant latestMessageAt = getLatestMessageAt(channel.getId());
        List<UserResponse> participants = findParticipants(channel);

        return channelMapper.toResponse(channel, latestMessageAt, participants);
    }

    private List<UserResponse> findParticipants(Channel channel) {
        if (channel.getType() == ChannelType.PUBLIC) {
            return List.of();
        }

        return readStatusRepository.findAllByChannelIdWithUser(channel.getId()).stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toResponse)
                .toList();
    }

    private Instant getLatestMessageAt(UUID channelId) {
        return messageRepository.findFirstByChannel_IdOrderByCreatedAtDesc(channelId)
                .map(Message::getCreatedAt)
                .orElse(null);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}