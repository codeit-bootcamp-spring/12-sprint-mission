package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("channelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelResponse createPublicChannel(ChannelCreateRequest request) {
        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.title(),
                request.userId(),
                request.description()
        );
        return convertToResponse(channelRepository.save(channel), null);
    }

    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(
                ChannelType.PRIVATE,
                null,
                request.userId(),
                null
        );
        Channel savedChannel = channelRepository.save(channel);

        request.memberIds().forEach(memberId -> {
            ReadStatus readStatus = new ReadStatus(memberId, savedChannel.getId());
            readStatusRepository.save(readStatus);
        });

        return convertToResponse(savedChannel, request.memberIds());
    }

    public ChannelResponse convertToResponse(Channel channel, List<UUID> memberIds) {
        return new ChannelResponse(
                channel.getId(),
                channel.getUserId(),
                channel.getTitle(),
                channel.getDescription(),
                channel.getType(),
                channel.getUpdatedAt(),
                memberIds
        );
    }

    @Override
    public ChannelResponse findById(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다."));

        List<UUID> memberIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            memberIds = readStatusRepository.findByChannelId(id).stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }
        return convertToResponse(channel, memberIds);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAll();
        List<ReadStatus> statuses = readStatusRepository.findAll();

        return channels.stream()
                .filter(channel -> {
                    // public은 전부 다 받음.
                    if (channel.getType() == ChannelType.PUBLIC) return true;

                    return statuses.stream()
                            //해당 channel에 속해있는 status인지 확인
                            .anyMatch(s -> s.getChannelId().equals(channel.getId())
                                    // 제시된 user와 id가 같은것만 추출.
                                    && s.getUserId().equals(userId));
                })
                .map(channel -> {
                    List<UUID> members = null;
                    if (channel.getType() == ChannelType.PRIVATE) {
                        members = statuses.stream()
                                .filter(s -> s.getChannelId().equals(channel.getId()))
                                .map(ReadStatus::getUserId)
                                .toList();
                    }
                    return convertToResponse(channel, members);
                })
                .toList();
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.id()).
                orElseThrow(() -> new NoSuchElementException("해당 재널이 존재하지 않습니다"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(request.title(), request.description());

        Channel updatedChannel = channelRepository.save(channel);
        return convertToResponse(updatedChannel, null);
    }

    @Override
    public void delete(UUID id) {
        if(!channelRepository.existsById(id)){
            throw new NoSuchElementException("해당 채널이 존재하지 않습니다.");
        }
        List<Message> messages = messageRepository.findByChannelId(id);
        messages.forEach(message -> messageRepository.delete(message.getId()));

        List<ReadStatus> statuses = readStatusRepository.findByChannelId(id);
        statuses.forEach(status -> readStatusRepository.delete(status.getId()));

        channelRepository.delete(id);
    }
}











