package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        validateType(type);
        validateName(name);
        validateDescription(description);
        validateDuplicateName(name);

        Channel channel = new Channel(type, name, description);
        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, ChannelType type, String name, String description) {
        validateType(type);
        validateName(name);
        validateDescription(description);
        Channel channel = findById(id);
        if (!channel.getName().equals(name)) {
            validateDuplicateName(name);
        }
        channel.update(type, name, description);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        channelRepository.delete(id);
    }

    private void validateType(ChannelType type) {
        if (type == null) {
            throw new IllegalArgumentException("채널 타입은 null일 수 없습니다.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 비어 있을 수 없습니다.");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("채널 설명은 비어 있을 수 없습니다.");
        }
    }

    private void validateDuplicateName(String name) {
        boolean exists = channelRepository.findAll().stream()
                .anyMatch(channel -> channel.getName().equals(name));
        if (exists) {
            throw new IllegalArgumentException("이미 같은 이름의 채널이 존재합니다.");
        }
    }
}
