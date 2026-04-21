package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreateDTO;
import com.sprint.mission.discodeit.dto.ChannelFindDTO;
import com.sprint.mission.discodeit.dto.ChannelUpdateDTO;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel createPublic(ChannelCreateDTO channelCreateDTO) {
        Channel channel = new Channel(ChannelType.PUBLIC, channelCreateDTO.getName(), channelCreateDTO.getDescription());
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivate(ChannelCreateDTO channelCreateDTO) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        for (User user : channelCreateDTO.getUsers()) {
            readStatusRepository.save(new ReadStatus(user.getId(), channel.getId()));
        }
        return channelRepository.save(channel);
    }

    @Override
    public ChannelFindDTO find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        ChannelFindDTO dto = ChannelFindDTO.from(channel);
        dto.setRecentMessageAt(findRecentMessageAt(channel.getId()));

        if (channel.getType() == ChannelType.PRIVATE) {
            dto.setUserIds(readStatusRepository.findByChannelId(channel.getId()).stream()
                    .map(ReadStatus::getUserId)
                    .toList()
            );
        }
        return dto;
    }

    @Override
    public List<ChannelFindDTO> findAllByUserId(UUID userId) {
        List<ChannelFindDTO> dtos = new ArrayList<>();
        dtos.addAll(channelRepository.findAll().stream()
                .filter(channel -> channel.getType().equals(ChannelType.PUBLIC))
                .map(channel -> {
                    ChannelFindDTO dto = ChannelFindDTO.from(channel);
                    dto.setRecentMessageAt(findRecentMessageAt(channel.getId()));
                    return dto;
                })
                .toList()
        );
        dtos.addAll(readStatusRepository.findByUserId(userId).stream()
                .map(readStatus -> find(readStatus.getChannelId()))
                .toList()
        );
        return dtos;
    }

    private Instant findRecentMessageAt(UUID channelId) {
        List<Message> messages = messageRepository.findByChannelId(channelId);
        if (messages.isEmpty()) {
            return null;
        }
        System.out.println(messages);
        Message message = messages.stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .orElseThrow(() -> new NoSuchElementException("Message with channel not found"));
        return message.getCreatedAt();
    }

    @Override
    public Channel update(ChannelUpdateDTO channelUpdateDTO) {
        Channel channel = channelRepository.findById(channelUpdateDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelUpdateDTO.getId() + " not found"));
        channel.update(channelUpdateDTO.getName(), channelUpdateDTO.getDescription());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        List<Message> messages = messageRepository.findByChannelId(channel.getId());
        for (Message message : messages) {
            messageRepository.deleteById(message.getId());
        }
        List<ReadStatus> readStatuses = readStatusRepository.findByChannelId(channel.getId());
        for (ReadStatus readStatus : readStatuses) {
            readStatusRepository.deleteById(readStatus.getId());
        }
        channelRepository.deleteById(channelId);
    }
}
