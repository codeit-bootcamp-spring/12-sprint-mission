package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.ReadStatus;
import com.sprint.mission.discodeit.domain.channel.Channel;
import com.sprint.mission.discodeit.domain.channel.ChannelType;
import com.sprint.mission.discodeit.domain.message.Message;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelResponse createPublicChannel(CreatePublicChannelRequest dto) {
        Channel channel = Channel.createPublic(dto.name(), dto.description());

        channelRepository.save(channel);

        return new ChannelResponse(channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                null,
                List.of()
        );
    }

    @Override
    public ChannelResponse createPrivateChannel(CreatePrivateChannelRequest dto) {
        Channel channel = Channel.createPrivate();
        channelRepository.save(channel);

        for (UUID userId : dto.participantIds()) {
            ReadStatus readStatus = new ReadStatus(userId, channel.getId(), Instant.MIN);
            readStatusRepository.save(readStatus);
        }

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                null,
                dto.participantIds()
        );
    }

    @Override
    public ChannelResponse findById(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);

        Instant latestMessageAt = getLatestMessageAt(channel.getId());

        List<UUID> participantUserIds = List.of();

        if (channel.getType() == ChannelType.PRIVATE) {
            participantUserIds = readStatusRepository.findAllByChannelId(channelId).stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                latestMessageAt,
                participantUserIds
        );
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<Channel> channelList = channelRepository.findAll();
        List<ChannelResponse> channelResponseList = new ArrayList<>();

        for (Channel channel : channelList) {
            Instant latestMessageAt = getLatestMessageAt(channel.getId());

            if (channel.getType() == ChannelType.PUBLIC) {
                ChannelResponse channelResponse = new ChannelResponse(
                        channel.getId(),
                        channel.getType(),
                        channel.getName(),
                        channel.getDescription(),
                        channel.getCreatedAt(),
                        latestMessageAt, List.of()
                );
                channelResponseList.add(channelResponse);
            } else if (channel.getType() == ChannelType.PRIVATE) {
                if (readStatusRepository.findByUserIdAndChannelId(userId, channel.getId()).isPresent()) {
                    List<UUID> participantUserIds = readStatusRepository.findAllByChannelId(channel.getId())
                            .stream().map(ReadStatus::getUserId).toList();

                    ChannelResponse channelResponse = new ChannelResponse(
                            channel.getId(),
                            channel.getType(),
                            channel.getName(),
                            channel.getDescription(),
                            channel.getCreatedAt(),
                            latestMessageAt,
                            participantUserIds
                    );
                    channelResponseList.add(channelResponse);
                }
            }
        }
        return channelResponseList;
    }

    @Override
    public ChannelResponse update(UUID channelId, ChannelUpdateRequest dto) {
        Channel channel = getChannelOrThrow(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("Private channel cannot be updated");
        }

        Instant latestMessageAt = getLatestMessageAt(channel.getId());

        channel.update(dto.newName(), dto.newDescription());
        channelRepository.save(channel);
        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                latestMessageAt,
                List.of()
        );
    }

    @Override
    public void delete(UUID channelId) {
        getChannelOrThrow(channelId);

        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        for (Message message : messages) {
            messageRepository.deleteById(message.getId());
        }

        List<ReadStatus> readStatusList = readStatusRepository.findAllByChannelId(channelId);
        for (ReadStatus readStatus : readStatusList) {
            readStatusRepository.deleteById(readStatus.getId());
        }

        channelRepository.deleteById(channelId);
    }

    private Instant getLatestMessageAt(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }
}

