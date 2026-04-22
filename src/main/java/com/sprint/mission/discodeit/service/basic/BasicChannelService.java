package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.data.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.data.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.data.dto.ChannelDto;
import com.sprint.mission.discodeit.data.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.channel.ReadStatus;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel create(PublicChannelCreateRequest request) {
        Channel channel = new Channel(request.type(), request.name(), request.description(), false);
        return channelRepository.save(channel);
    }

    @Override
    public Channel create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(request.type(), null, null, true);
        channelRepository.save(channel);

        List<UUID> userIdList = request.userIdList();
        for (UUID id : userIdList) {
            ReadStatus newReadStatus = new ReadStatus(id, channel.getId());
            readStatusRepository.save(newReadStatus);
        }

        return channel;
    }

    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            throw new IllegalArgumentException("해당 id를 가진 채널이 없습니다.");
        }

        return toDto(channel, getParticipantUserIds(channel), getLatestMessageAt(channel.getId()));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatusList = readStatusRepository.findAllByUserId(userId);
        Set<UUID> userPrivateChannelIds = getPrivateChannelIds(readStatusList);

        List<Channel> channelList = channelRepository.findAll();
        List<ChannelDto> resultList = new ArrayList<>();

        for (Channel ch : channelList) {
            if (canAccessChannel(ch, userPrivateChannelIds)) {
                resultList.add(toDto(ch, getParticipantUserIds(ch), getLatestMessageAt(ch.getId())));
            }
        }

        return resultList;
    }

    private List<UUID> getParticipantUserIds(Channel channel) {
        if (!channel.isPrivate()) {
            return Collections.emptyList();
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());
        List<UUID> participantUserIds = new ArrayList<>();

        for (ReadStatus readStatus : readStatuses) {
            participantUserIds.add(readStatus.getUserId());
        }

        return participantUserIds;
    }

    private Instant getLatestMessageAt(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        Instant latestMessageAt = null;

        for (Message message : messages) {
            Instant createdAt = message.getCreatedAt();
            if (latestMessageAt == null || createdAt.isAfter(latestMessageAt)) {
                latestMessageAt = createdAt;
            }
        }

        return latestMessageAt;
    }

    private Set<UUID> getPrivateChannelIds(List<ReadStatus> readStatusList) {
        Set<UUID> channelIds = new HashSet<>();

        for (ReadStatus readStatus : readStatusList) {
            channelIds.add(readStatus.getChannelId());
        }

        return channelIds;
    }

    ///user가 참여한 privateChannel목록에 현재 채널이 존재하면 추가
    private boolean canAccessChannel(Channel channel, Set<UUID> userPrivateChannelIds) {
        if (!channel.isPrivate()) {
            return true;
        }

        return userPrivateChannelIds.contains(channel.getId());
    }

    @Override
    public Channel update(UUID id, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(id).orElse(null);

        if (channel == null) {
            throw new IllegalArgumentException("해당 id를 가진 채널이 없습니다.");
        }

        if (channel.isPrivate()) {
            throw new IllegalArgumentException("private 채널은 수정 불가.");
        }

        channel.update(request.type(), request.name(), request.description());
        return channelRepository.save(channel);
    }

    @Override
    public Channel delete(UUID id) {
        readStatusRepository.deleteByChannelId(id);
        messageRepository.deleteAllByChannelId(id);
        return channelRepository.deleteById(id);
    }

    private ChannelDto toDto(Channel channel, List<UUID> participantUserIdList, Instant latestMessage) {
        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                participantUserIdList,
                latestMessage
        );
    }
}