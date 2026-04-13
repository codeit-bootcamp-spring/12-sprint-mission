package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Primary
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        return channelRepository.save(channel);
    }

    @Override
    public Channel find(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, ChannelType type, String name, String description) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));

        channel.update(type, name, description);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        if (!channelRepository.existsById(id)) {
            throw new NoSuchElementException("Channel with id " + id + " not found");
        }
        channelRepository.deleteById(id);
    }
}