package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelCategory;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Primary
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelDto createPublic(PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelCategory.PUBLIC, request.getName(), request.getDescription());
        channelRepository.save(channel);
        return toDto(channel);
    }


    @Override
    public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelCategory.PRIVATE, null, null);
        channelRepository.save(channel);

        request.getParticipantIds().forEach(userId -> {
            ReadStatus readStatus = new ReadStatus(
                    UUID.randomUUID(),
                    userId,
                    channel.getId(),
                    Instant.now()
            );
            readStatusRepository.save(readStatus);
        });

        return toDto(channel);
    }

    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel을 찾을 수 없습니다: " + channelId));
        return toDto(channel);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID> myPrivateChannelIds = readStatusRepository.findByUserId(userId)
                .stream()
                .map(ReadStatus::getChannelId)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getCategory() == ChannelCategory.PUBLIC
                                || myPrivateChannelIds.contains(channel.getId())
                )
                .map(this::toDto)
                .toList();
    }

    @Override
    public ChannelDto update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel을 찾을 수 없습니다: " + channelId));
        if (channel.getCategory() == ChannelCategory.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(request.getNewName(), request.getNewDescription());
        channelRepository.save(channel);
        return toDto(channel);
    }

    @Override
    public void delete(UUID channelId) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel을 찾을 수 없습니다: " + channelId));
        // Deleting Related Message
        messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .forEach(m -> messageRepository.deleteById(m.getId()));
        // Deleting Related ReadStatus
        readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .forEach(rs -> readStatusRepository.deleteById(rs.getId()));

        channelRepository.deleteById(channelId);
    }

    @Override
    public Channel create(String general, String 공지, ChannelCategory channelCategory) {
        return null;
    }

    private ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channel.getId()))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        List<UUID> participantIds = null;
        if (channel.getCategory() == ChannelCategory.PRIVATE) {
            participantIds = readStatusRepository.findAll().stream()
                    .filter(rs -> rs.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return new ChannelDto(
                channel.getId(),
                channel.getCategory(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                lastMessageAt,
                participantIds
        );
    }
}