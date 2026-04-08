package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepo;

    public BasicChannelService(ChannelRepository channelRepo) {
        this.channelRepo = channelRepo;
    }

    @Override
    public Channel create(ChannelType channelType, String channelName, String description) {
        if (channelType == null) {
            throw new IllegalArgumentException("채널 타입은 필수입니다.");
        }
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("채널명에 공백을 입력할 수 없습니다.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("채널설명에 공백을 입력할 수 없습니다.");
        }

        Channel channel = new Channel(channelType, channelName, description);
        return channelRepo.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = channelRepo.findById(id);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = channelRepo.findAll();
        channels.sort((c1, c2) -> Long.compare(c1.getCreatedAt(), c2.getCreatedAt()));
        return channels;
    }

    @Override
    public Channel updateChannelType(UUID id, ChannelType channelType) {
        if (channelType == null) {
            throw new IllegalArgumentException("채널 타입은 필수입니다.");
        }
        Channel channel = findById(id);
        channel.updateChannelType(channelType);
        return channelRepo.save(channel);
    }

    @Override
    public Channel updateChannelName(UUID id, String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("채널명에 공백을 입력할 수 없습니다.");
        }
        Channel channel = findById(id);
        channel.updateChannelName(channelName);
        return channelRepo.save(channel);
    }

    @Override
    public Channel updateChannelDescription(UUID id, String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("채널 설명에 공백을 입력할 수 없습니다.");
        }
        Channel channel = findById(id);
        channel.updateDescription(description);
        return channelRepo.save(channel);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        channelRepo.deleteById(id);
    }
}
