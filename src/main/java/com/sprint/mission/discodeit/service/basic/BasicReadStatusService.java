package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readStatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readStatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(CreateReadStatusRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User with id " + request.userId() + " not found");
        }

        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel with id " + request.channelId() + " not found");
        }

        readStatusRepository.findAll().stream()
                .filter(readStatus ->
                        readStatus.getUserId().equals(request.userId()) &&
                        readStatus.getChannelId().equals(request.channelId()))
                .findAny()
                .ifPresent(readStatus -> {
                    throw new IllegalArgumentException("ReadStatus already exists");
                });

        ReadStatus readStatus = new ReadStatus(
                UUID.randomUUID(),
                request.userId(),
                request.channelId()
        );
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public ReadStatus update(UpdateReadStatusRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.readStatusId())
                .orElseThrow(() -> new NoSuchElementException(
                        "ReadStatus with id " + request.readStatusId() + " not found"
                ));

        readStatus.update(request.newLastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException(
                        "ReadStatus with id " + readStatusId + " not found"
                ));

        readStatusRepository.deleteById(readStatusId);
    }
}
