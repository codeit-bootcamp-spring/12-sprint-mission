package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel save(Channel channel) {
        if (channel == null) {
            System.err.println("입력된 채널 객체가 null입니다.");
            return null;
        }

        if (channel.getName() == null || channel.getName().isBlank()) {
            System.err.println("채널 이름은 필수 입력 항목입니다.");
            return null;
        }

        if (channel.getDescription() == null) {
            System.err.println("채널 설명은 필수 입력 항목입니다.");
            return null;
        }

        if (channelRepository.findById(channel.getId()).isPresent()) {
            System.err.println("이미 존재하는 ID의 채널입니다.");
            return null;
        }

        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("해당 ID의 채널을 찾을 수 없습니다."));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        return channelRepository.update(id, name, description);
    }

    @Override
    public Channel delete(UUID id) {
        return channelRepository.delete(id);
    }
}
