package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.channel.ChannelCreatePrivateRequest;
import com.sprint.mission.discodeit.dto.data.channel.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.dto.data.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.data.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelResponse createPublic(ChannelCreatePublicRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        channelRepository.save(channel);
        return toResponse(channel);
    }

    @Override
    public ChannelResponse createPrivate(ChannelCreatePrivateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        for (UUID userId : request.participantUserIds()) {
            ReadStatus readStatus = new ReadStatus(userId, channel.getId());
            readStatusRepository.save(readStatus);
        }

        return toResponse(channel);
    }

    @Override
    public ChannelResponse find(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다."));
        return toResponse(channel);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<UUID> myPrivateChannelIds = readStatusRepository.findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .map(ReadStatus::getChannelId)
                .toList();


        return channelRepository.findAll().stream()
                .filter(c -> c.getType() == ChannelType.PUBLIC || myPrivateChannelIds.contains(c.getId()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChannelResponse update(UUID id, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다."));


        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(channel.getType(), request.name(), request.description());
        channelRepository.save(channel);
        return toResponse(channel);
    }

    @Override
    public void delete(UUID id) {
        messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(id))
                .forEach(m -> messageRepository.deleteById(m.getId()));

        readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannelId().equals(id))
                .forEach(rs -> readStatusRepository.deleteById(rs.getId()));

        channelRepository.deleteById(id);
    }

    private ChannelResponse toResponse(Channel channel) {
        Instant lastMessageAt = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channel.getId()))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        List<UUID> participantIds = new ArrayList<>();
        if (channel.getType() == ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findAll().stream()
                    .filter(rs -> rs.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return ChannelResponse.builder()
                .id(channel.getId())
                .type(channel.getType())
                .name(channel.getName())
                .description(channel.getDescription())
                .lastMessageAt(lastMessageAt)
                .participantUserIds(participantIds.isEmpty() ? null : participantIds)
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .build();
    }
}