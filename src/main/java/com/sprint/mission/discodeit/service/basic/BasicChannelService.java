package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserService userService;

    public BasicChannelService(ChannelRepository channelRepository, UserService userService) {
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    @Override
    public Channel save(Channel channel) {
        if (channelRepository.findById(channel.getId()) != null)
            throw new IllegalStateException("이미 동일한 id의 Channel이 존재합니다.");
        validateChannel(channel);
        userService.findById(channel.getAuthor().getId());
        return channelRepository.save(channel);
    }

    private void validateChannel(Channel channel) {
        if (channel.getTitle() == null || channel.getTitle().isBlank())
            throw new IllegalArgumentException("제목은 필수 입력 항목 입니다.");
        if (channel.getCategory() == null || channel.getCategory().isBlank())
            throw new IllegalArgumentException("카테고리는 필수 입력 항목 입니다.");
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) throw new NoSuchElementException("해당 Channel이 존재하지 않습니다.");
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = channelRepository.findAll();
        System.out.println("현재 등록 Channel : " + channels.size() + "개");
        return channels;
    }

    @Override
    public Channel update(Channel channel) {
        Channel updatechannel = channelRepository.findById(channel.getId());
        if (updatechannel == null) throw new NoSuchElementException("해당 Channel이 존재하지 않습니다.");
        updatechannel.update(channel);
        channelRepository.save(updatechannel);
        return updatechannel;
    }

    @Override
    public void delete(UUID id) {
        if (channelRepository.findById(id) == null) throw new NoSuchElementException("해당 Channel이 존재하지 않습니다.");
        channelRepository.delete(id);
    }
}











