package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel createPublic(CreatePublicChannelRequest request) {
        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.name(),
                request.description());
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivate(CreatePrivateChannelRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE);
        Channel savedChannel = channelRepository.save(channel);

        List<User> users = userRepository.findAll().stream()
                .filter(user -> request.userIds().contains(user.getId()))
                .toList();

        for (User user : users) {
            ReadStatus readStatus = new ReadStatus(
                    UUID.randomUUID(),
                    savedChannel.getId(),
                    user.getId()
            );
            readStatusRepository.save(readStatus);
        }
        return savedChannel;
    }

    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        Instant latesMessageAt = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        List<UUID> userIds = List.of();

        if (channel.getType() == ChannelType.PRIVATE) {
            userIds = readStatusRepository.findAll().stream()
                    .filter(rs -> rs.getChannelId().equals(channelId))
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                latesMessageAt,
                userIds
        );
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) {
                        return true;
                    }

                    return readStatusRepository.findAll().stream()
                            .anyMatch(rs ->
                                    rs.getChannelId().equals(channel.getId()) &&
                                    rs.getUserId().equals(userId)
                            );

                })
                .map(channel -> {
                    Instant latestMessageAt = messageRepository.findAll().stream()
                            .filter(message -> message.getChannelId().equals(channel.getId()))
                            .map(Message::getCreatedAt)
                            .max(Instant::compareTo)
                            .orElse(null);

                    List<UUID> userIds = List.of();

                    if (channel.getType() == ChannelType.PRIVATE) {
                        userIds = readStatusRepository.findAll().stream()
                                .filter(rs -> rs.getChannelId().equals(channel.getId()))
                                .map(ReadStatus::getUserId)
                                .toList();
                    }

                    return new ChannelDto(
                            channel.getId(),
                            channel.getType(),
                            channel.getName(),
                            channel.getDescription(),
                            channel.getCreatedAt(),
                            channel.getUpdatedAt(),
                            latestMessageAt,
                            userIds
                    );
                })
                .toList();
    }

    @Override
    public Channel update(UpdateChannelRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel with id " + request.channelId() + " not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("Cannot update private channel");
        }

        channel.update(request.name(), request.description());
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        messageRepository.findAll().stream()
                    .filter(message -> message.getChannelId().equals(channelId))
                    .forEach(message -> messageRepository.deleteById(message.getId()));

        readStatusRepository.findAll().stream()
                    .filter(rs -> rs.getChannelId().equals(channelId))
                    .forEach(rs -> readStatusRepository.deleteById(rs.getId()));

        channelRepository.deleteById(channelId);
    }
}