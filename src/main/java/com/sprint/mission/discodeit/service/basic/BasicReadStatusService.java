package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicReadStatusService(
            @Qualifier("jCFReadStatusRepository") ReadStatusRepository readStatusRepository,
            @Qualifier("jCFUserRepository") UserRepository userRepository,
            @Qualifier("jCFChannelRepository") ChannelRepository channelRepository
    ) {
        this.userRepository = userRepository;
        this.readStatusRepository = readStatusRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateRequestDto createDto) {
        if (!userRepository.existsById(createDto.userId())) {
            throw new NoSuchElementException("User not found with id " + createDto.userId());
        }

        if (!channelRepository.existsById(createDto.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + createDto.channelId());
        }

        if (readStatusRepository.existsByChannelIdAndUserId(createDto.channelId(), createDto.userId())) {
            throw new IllegalArgumentException("ReadStatus already exists for user " + createDto.userId() + " and channel " + createDto.channelId());
        }

        ReadStatus readStatus = new ReadStatus(createDto.userId(), createDto.channelId());
        readStatusRepository.save(readStatus);

        return ReadStatusResponseDto.from(readStatus);
    }

    @Override
    public ReadStatusResponseDto find(UUID readStatusId) {

        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + readStatusId));

        return ReadStatusResponseDto.from(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
        List<ReadStatusResponseDto> dtos = new ArrayList<>();

        for (ReadStatus readStatus : readStatuses) {
            dtos.add(ReadStatusResponseDto.from(readStatus));
        }

        return dtos;
    }

    @Override
    public ReadStatusResponseDto update(ReadStatusUpdateRequestDto userDto) {
        ReadStatus readStatus = readStatusRepository.findById(userDto.id())
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + userDto.id()));

        readStatus.update(userDto.updatedAt());
        readStatusRepository.save(readStatus);

        return ReadStatusResponseDto.from(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus not found with id " + readStatusId);
        }

        readStatusRepository.deleteById(readStatusId);
    }
}
