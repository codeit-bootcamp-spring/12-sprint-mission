package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    // 필요한 Repository 인터페이스를 필드로 선언
    private final ChannelRepository channelRepository;

    // 생성자를 통해 초기화
    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(String name) {
        // 비즈니스 로직
        Channel channel = new Channel(name);

        // 저장 로직
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID id, String name) {
        Channel channel = channelRepository.findById(id);
        if (channel != null) {
            // 비즈니스 로직
            channel.update(name);

            // 저장 로직
            channelRepository.update(channel);
        }
        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        channelRepository.delete(id);
    }
}