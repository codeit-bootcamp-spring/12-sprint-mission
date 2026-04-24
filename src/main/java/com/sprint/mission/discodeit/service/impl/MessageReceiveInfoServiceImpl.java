package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.data.MessageReceiveInfoDto;
import com.sprint.mission.discodeit.entity.MessageReceiveInfo;
import com.sprint.mission.discodeit.repository.MessageReceiveInfoRepository;
import com.sprint.mission.discodeit.service.MessageReceiveInfoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageReceiveInfoServiceImpl implements MessageReceiveInfoService {

    private final MessageReceiveInfoRepository messageReceiveInfoRepository;

    public MessageReceiveInfoServiceImpl(MessageReceiveInfoRepository messageReceiveInfoRepository) {
        this.messageReceiveInfoRepository = messageReceiveInfoRepository;
    }

    @Override
    public MessageReceiveInfoDto create(MessageReceiveInfoDto dto) {
        MessageReceiveInfo entity = new MessageReceiveInfo(
                dto.channelId(),
                dto.userId(),
                dto.read(),
                dto.alarmOn()
        );
        messageReceiveInfoRepository.save(entity);
        return new MessageReceiveInfoDto(
                entity.getId(),
                entity.getChannelId(),
                entity.getUserId(),
                entity.isRead(),
                entity.isAlarmOn()
        );
    }
    @Override
    public MessageReceiveInfoDto update(UUID id, MessageReceiveInfoDto dto) {
        MessageReceiveInfo entity = messageReceiveInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MessageReceiveInfo not found"));
        entity.setChannelId(dto.channelId());
        entity.setUserId(dto.userId());
        entity.setRead(dto.read());
        entity.setAlarmOn(dto.alarmOn());
        messageReceiveInfoRepository.save(entity);
        return new MessageReceiveInfoDto(
                entity.getId(),
                entity.getChannelId(),
                entity.getUserId(),
                entity.isRead(),
                entity.isAlarmOn()
        );
    }

    @Override
    public List<MessageReceiveInfoDto> findByUserId(UUID userId) {
        return messageReceiveInfoRepository.findByUserId(userId)
                .stream()
                .map(entity -> new MessageReceiveInfoDto(
                        entity.getId(),
                        entity.getChannelId(),
                        entity.getUserId(),
                        entity.isRead(),
                        entity.isAlarmOn()
                ))
                .collect(Collectors.toList());
    }
}

