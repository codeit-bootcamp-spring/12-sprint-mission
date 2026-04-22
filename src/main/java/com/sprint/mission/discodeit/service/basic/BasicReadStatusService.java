package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.ReadStatus;
import com.sprint.mission.discodeit.domain.user.UserStatus;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest dto) {
        if (!channelRepository.existsById(dto.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + dto.channelId());
        }

        if (!userRepository.existsById(dto.userId())) {
            throw new NoSuchElementException("User not found with id " + dto.userId());
        }

        if (readStatusRepository.findByUserIdAndChannelId(dto.userId(), dto.channelId()).isPresent()) {
            throw new IllegalStateException("Read status already exists for user id " + dto.userId()
                    + " and channel id " + dto.channelId());
        }

        ReadStatus readStatus = new ReadStatus(dto.userId(), dto.channelId(), dto.lastReadAt());
        readStatusRepository.save(readStatus);

        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt(),
                readStatus.getCreatedAt()
        );
    }

    @Override
    public ReadStatusResponse findById(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + id + " not found"));

        return new ReadStatusResponse(readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt(),
                readStatus.getCreatedAt()
        );
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> new ReadStatusResponse(
                        readStatus.getId(),
                        readStatus.getUserId(),
                        readStatus.getChannelId(),
                        readStatus.getLastReadAt(),
                        readStatus.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest dto) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));

        readStatus.update(dto.newReadAt());
        readStatusRepository.save(readStatus);

        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt(),
                readStatus.getCreatedAt()
        );
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        readStatusRepository.deleteById(id);
    }
}
