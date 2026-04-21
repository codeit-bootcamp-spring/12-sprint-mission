package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public ChannelResponse createPublic(PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        return toResponse(channelRepository.save(channel));
    }

    @Override
    public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
        Set<UUID> participantIds = new LinkedHashSet<>(request.participantUserIds());
        if (participantIds.isEmpty()) {
            throw new IllegalArgumentException("private channel participants cannot be empty");
        }
        for (UUID userId : participantIds) {
            if (!userRepository.existsById(userId)) {
                throw new IllegalArgumentException("User not found : " + userId);
            }
        }
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel savedChannel = channelRepository.save(channel);

        for(UUID userId : participantIds) {
            ReadStatus readStatus = new ReadStatus(userId, savedChannel.getId(), Instant.now());
            readStatusRepository.save(readStatus);
        }
        return toResponse(savedChannel);
    }

    @Override
    public ChannelResponse findById(UUID channelId) {
        return toResponse(getChannel(channelId));
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found : " + userId);
        }
        Set<UUID> privateVisibleChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .collect(java.util.stream.Collectors.toSet());
        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC ||
                        privateVisibleChannelIds.contains(channel.getId()))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        Channel channel = getChannel(request.channelId());
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }
        channel.update(request.newName(), request.newDescription());
        return toResponse(channelRepository.save(channel));
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = getChannel(channelId);
        List<Message> messages = messageRepository.findAllByChannelId(channel.getId());
        for (Message message : messages) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                if (binaryContentRepository.existsById(attachmentId)) {
                    binaryContentRepository.deleteById(attachmentId);
                }
            }
            messageRepository.deleteById(message.getId());
        }
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channelId);
        for (ReadStatus readStatus : readStatuses) {
            readStatusRepository.deleteById(readStatus.getId());
        }
        channelRepository.deleteById(channelId);
    }

    private Channel getChannel(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found : " + channelId));
    }

    private ChannelResponse toResponse(Channel channel) {
        Instant latestMessageAt = messageRepository.findAllByChannelId(channel.getId()).stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
        List<UUID> participantUserIds = channel.getType() == ChannelType.PRIVATE
                ? readStatusRepository.findAllByChannelId(channel.getId()).stream()
                    .map(ReadStatus::getUserId)
                    .toList()
                : List.of();
        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantUserIds,
                latestMessageAt,
                channel.getCreatedAt(),
                channel.getUpdatedAt()
        );
    }
}
