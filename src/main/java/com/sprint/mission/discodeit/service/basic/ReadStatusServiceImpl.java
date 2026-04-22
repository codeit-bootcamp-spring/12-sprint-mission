package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Primary
@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        // User Confirmation
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User를 찾을 수 없습니다: " + request.getUserId()));

        // Channel Confirmation
        channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("Channel을 찾을 수 없습니다: " + request.getChannelId()));

        // duplicate check
        boolean exists = readStatusRepository.findByUserId(request.getUserId()).stream()
                .anyMatch(rs -> rs.getChannelId().equals(request.getChannelId()));
        if (exists) {
            throw new IllegalArgumentException("이미 존재하는 ReadStatus입니다.");
        }

        ReadStatus readStatus = new ReadStatus(
                UUID.randomUUID(),
                request.getUserId(),
                request.getChannelId(),
                Instant.now()
        );
        return readStatusRepository.save(readStatus);
    }


    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus를 찾을 수 없습니다: " + id));
        readStatus.updateReadAt();
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus를 찾을 수 없습니다: " + id));
        readStatusRepository.deleteById(id);

    }
}
