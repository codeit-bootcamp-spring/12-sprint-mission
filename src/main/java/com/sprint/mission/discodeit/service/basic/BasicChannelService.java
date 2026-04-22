package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(
            @Qualifier("jCFChannelRepository") ChannelRepository channelRepository,
            @Qualifier("jCFMessageRepository") MessageRepository messageRepository,
            @Qualifier("jCFReadStatusRepository") ReadStatusRepository readStatusRepository
    ) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.readStatusRepository = readStatusRepository;
    }

    @Override
    public ChannelResponseDto publicChannelCreate(PublicChannelCreateRequestDto dto) {
        Channel channel = Channel.createPublicChannel(dto.type(), dto.name(), dto.description());
        channelRepository.save(channel);
        return ChannelResponseDto.publicChannelFrom(channel, null);
    }

    @Override
    public ChannelResponseDto privateChannelCreate(PrivateChannelCreateRequestDto dto) {
        Channel channel = Channel.createPrivateChannel(dto.type());
        Channel savedChannel = channelRepository.save(channel);

        for (UUID userId : dto.userIds()) {
            ReadStatus readStatus = new ReadStatus(userId, savedChannel.getId());
            readStatusRepository.save(readStatus);
        }
        return ChannelResponseDto.privateChannelFrom(savedChannel, dto.userIds(), null);
    }


    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        Instant lastMessageAt = messageRepository.findLatestByChannelId(channelId)
                .map(Message::getCreatedAt)
                .orElse(null);

        List<UUID> userIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            userIds = readStatusRepository.findAllByChannelId(channelId);
        }

        return ChannelResponseDto.from(channel, userIds, lastMessageAt);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAll();

        List<ChannelResponseDto> responseDtos = new ArrayList<>();

        for (Channel channel : channels) {
            if (channel.getType() == ChannelType.PRIVATE) {
                boolean isParticipant = readStatusRepository.existsByChannelIdAndUserId(channel.getId(), userId);
                if (!isParticipant) {
                    continue;
                }
            }
            Instant lastMessageAt = messageRepository.findLatestByChannelId(channel.getId())
                    .map(Message::getCreatedAt)
                    .orElse(null);

            List<UUID> userIds = null;
            if (channel.getType() == ChannelType.PRIVATE) {
                userIds = readStatusRepository.findAllByChannelId(channel.getId());
            }

            responseDtos.add(ChannelResponseDto.from(channel, userIds, lastMessageAt));
        }

        return responseDtos;
    }


    @Override
    public ChannelResponseDto update(ChannelUpdateRequestDto dto) {
        Channel channel = channelRepository.findById(dto.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + dto.channelId() + " not found"));
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("Cannot update a private channel, channel Id : " + dto.channelId());
        }
        channel.update(dto.name(), dto.description());
        channelRepository.save(channel);

        return ChannelResponseDto.from(channel, null, null);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        readStatusRepository.deleteAllByChannelId(channelId);
        messageRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }
}
