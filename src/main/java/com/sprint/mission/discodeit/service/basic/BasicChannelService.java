package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Channel create(PublicChannelCreateRequest request) {
        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.name(),
                request.description()
        );
        return channelRepository.save(channel);
    }

    @Override
    @Transactional
    public Channel create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        List<User> participants = userRepository.findAllById(request.participantIds());

        if (participants.size() != request.participantIds().size()) {
            throw new NoSuchElementException("Some users were not found");
        }
        List<ReadStatus> readStatuses = participants.stream()
                .map(user -> new ReadStatus(user, createdChannel, Instant.MIN))
                .toList();
        readStatusRepository.saveAll(readStatuses);
        return createdChannel;
    }

    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        Map<UUID, Instant> lastMessageAtMap = findLastMessageAtMap(List.of(channel.getId()));

        Map<UUID, List<UserDto>> participantsMap = channel.getType() == ChannelType.PRIVATE
                ? findParticipantsMap(List.of(channel.getId()))
                : Map.of();
        return toDto(channel, lastMessageAtMap, participantsMap);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID> subscribedChannelIds = readStatusRepository.findAllByUser_Id(userId).stream()
                .map(readStatus -> readStatus.getChannel().getId())
                .toList();

        List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);

        List<Channel> subscribedChannels = subscribedChannelIds.isEmpty()
                ? List.of()
                : channelRepository.findAllById(subscribedChannelIds);

        LinkedHashMap<UUID, Channel> channelMap = new LinkedHashMap<>();
        publicChannels.forEach(channel -> channelMap.put(channel.getId(), channel));
        subscribedChannels.forEach(channel -> channelMap.put(channel.getId(), channel));

        List<Channel> channels = new ArrayList<>(channelMap.values());

        List<UUID> channelIds = channels.stream()
                .map(Channel::getId)
                .toList();

        Map<UUID, Instant> lastMessageAtMap = findLastMessageAtMap(channelIds);

        List<UUID> privateChannelIds = channels.stream()
                .filter(channel -> channel.getType() == ChannelType.PRIVATE)
                .map(Channel::getId)
                .toList();

        Map<UUID, List<UserDto>> participantsMap = findParticipantsMap(privateChannelIds);

        return channels.stream()
                .map(channel -> toDto(channel, lastMessageAtMap, participantsMap))
                .toList();
    }

    @Override
    @Transactional
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }
        channel.update(request.newName(), request.newDescription());
        return channel;
    }

    @Override
    @Transactional
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        readStatusRepository.deleteAllByChannel_Id(channelId);
        messageRepository.deleteAllByChannel_Id(channelId);
        channelRepository.delete(channel);
    }

    private Map<UUID, Instant> findLastMessageAtMap(List<UUID> channelIds) {
        if (channelIds == null || channelIds.isEmpty()) {
            return Map.of();
        }
        return messageRepository.findLastMessageAtsByChannelIds(channelIds).stream()
                .collect(Collectors.toMap(
                        MessageRepository.ChannelLastMessageAtProjection::getChannelId,
                        MessageRepository.ChannelLastMessageAtProjection::getLastMessageAt
                ));
    }

    private Map<UUID, List<UserDto>> findParticipantsMap(List<UUID> privateChannelIds) {
        if (privateChannelIds == null || privateChannelIds.isEmpty()) {
            return Map.of();
        }
        return readStatusRepository.findAllByChannel_IdIn(privateChannelIds).stream()
                .collect(Collectors.groupingBy(
                        readStatus -> readStatus.getChannel().getId(),
                        Collectors.mapping(
                                readStatus -> userMapper.toDto(readStatus.getUser()),
                                Collectors.toList()
                        )
                ));
    }

    private ChannelDto toDto(
            Channel channel,
            Map<UUID, Instant> lastMessageAtMap,
            Map<UUID, List<UserDto>> participantsMap) {
        Instant lastMessageAt = lastMessageAtMap.getOrDefault(
                channel.getId(),
                Instant.MIN
        );

        List<UserDto> participants = channel.getType() == ChannelType.PRIVATE
                ? participantsMap.getOrDefault(channel.getId(), List.of())
                : List.of();
        return channelMapper.toDto(channel, participants, lastMessageAt);
    }
}
