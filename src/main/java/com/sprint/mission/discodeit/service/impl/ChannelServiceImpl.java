package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service("channelServiceImpl")
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;


    @Override
    public ChannelDto create(PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();

        Channel channel = Channel.builder()
                .name(name)
                .description(description)
                .type(ChannelType.PUBLIC)
                .build();

        Channel savedchannel = channelRepository.save(channel);


        return ChannelDto.from(savedchannel, null, null);
    }

    @Override
    public ChannelDto create(PrivateChannelCreateRequest request) {
        List<UUID> memberIds = request.memberIds();

        if (memberIds == null || memberIds.isEmpty()) {
            throw new IllegalArgumentException("memberIds cannot be null or empty");
        }
        if (memberIds.size() < 2) {
            throw new IllegalArgumentException("Private 채널은 2명 이상부터 가능합니다.");
        }
        if (new HashSet<>(memberIds).size() != memberIds.size()) {
            throw new IllegalArgumentException("중복된 멤버가 있습니다.");
        }

        List<User> members = new ArrayList<>();
        for (UUID memberId : memberIds) {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + memberId));
            members.add(user);
        }

        Channel channel = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();

        channel = channelRepository.save(channel);

        for (User member : members) {
            ReadStatus readStatus = ReadStatus.builder()
                    .userId(member.getId())
                    .channelId(channel.getId())
                    .build();
            readStatusRepository.save(readStatus);
        }

        return ChannelDto.from(channel, null, null);
    }

    @Override
    public ChannelDto findById(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        // 2. 해당 채널의 가장 최근 메시지 시간 조회
        //    - 메시지 없으면 null
        List<Message> messages = messageRepository.findAllByChannelId(channel.getId());
        Instant lastMessageAt = messages.stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        // 3. 채널이 PRIVATE인지 확인
        //    - PRIVATE면 ReadStatus 목록 조회
        //    - ReadStatus에서 userId만 꺼내기
        //    - PUBLIC이면 memberIds는 null 또는 빈 리스트
        List<UUID> memberIds = new ArrayList<>();
        if (channel.getType() == ChannelType.PRIVATE) {
            List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());
            memberIds = readStatuses.stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return ChannelDto.from(channel, lastMessageAt, memberIds);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        List<Channel> channels = channelRepository.findAll();
        List<ChannelDto> result = new ArrayList<>();

        for (Channel channel : channels) {
            boolean visible = false;

            if (channel.getType() == ChannelType.PUBLIC) {
                visible = true;
            } else if (channel.getType() == ChannelType.PRIVATE) {
                List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());

                visible = readStatuses.stream()
                        .anyMatch(readStatus -> readStatus.getUserId().equals(userId));
            }

            if (!visible) {
                continue;
            }

            Instant lastMessageAt = null;
            List<UUID> memberIds = new ArrayList<>();

            List<Message> messages = messageRepository.findAllByChannelId(channel.getId());
            lastMessageAt = messages.stream()
                    .map(Message::getCreatedAt)
                    .max(Instant::compareTo)
                    .orElse(null);

            if (channel.getType() == ChannelType.PRIVATE) {
                List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());

                memberIds = readStatuses.stream()
                        .map(ReadStatus::getUserId)
                        .toList();
            }

            result.add(ChannelDto.from(channel, lastMessageAt, memberIds));
        }

        return result;
    }

    @Override
    public ChannelDto update(ChannelUpdateRequest request) {
        UUID id = request.id();
        String name = request.name();
        String description = request.description();

        Channel channel = channelRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("id가 없습니다."));
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        channel.update(name, description);
        channel = channelRepository.save(channel);

        return ChannelDto.from(channel, null, null);
    }

    @Override
    public ChannelDto delete(UUID id) {
        Channel channel = channelRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("id가 없습니다."));

        for (Message message : messageRepository.findAllByChannelId(channel.getId())) {
            messageRepository.delete(message.getId());
        }
        for (ReadStatus readStatus : readStatusRepository.findAllByChannelId(channel.getId())) {
            readStatusRepository.delete(readStatus.getId());
        }

        channelRepository.delete(channel.getId());

        return ChannelDto.from(channel, null, null);
    }
}
