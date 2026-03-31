package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.CreateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.util.FileSerialization;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(CreateChannelRequest request) {
        Channel channel = new Channel(request.channelOwnerId(), request.type(), request.name(), request.isPrivate());
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel findChannelByName(String name) {
        Optional<Channel> channel = channelRepository.findByName(name);

        if (channel.isPresent()) {
            return channel.get();
        } else {
            throw new IllegalArgumentException("해당 이름을 가진 채널 없음");
        }
    }

    @Override
    public List<Channel> findAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel changeChannelName(UUID id, String name) {
        Optional<Channel> opCh = channelRepository.findById(id);

        if (opCh.isEmpty()) {
            throw new IllegalArgumentException("해당 id를 가진 채널 없음.");
        }

        Channel ch = opCh.get();
        ch.update(ch.getChannelOwnerId(), ch.getType(), name, ch.isPrivate());

        return channelRepository.save(ch);
    }

    @Override
    public Channel deleteChannel(UUID id) {
        return channelRepository.delete(id);
    }
}