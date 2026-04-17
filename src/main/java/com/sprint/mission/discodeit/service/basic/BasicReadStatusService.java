package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.data.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.data.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        if (!channelRepository.existsById(request.channelId())) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        boolean isDuplicate = readStatusRepository.findAll().stream()
                .anyMatch(rs -> rs.getUserId().equals(request.userId()) && rs.getChannelId().equals(request.channelId()));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 해당 채널에 대한 읽음 상태 정보가 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        readStatusRepository.save(readStatus);

        return toResponse(readStatus);
    }

    @Override
    public ReadStatusResponse find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found"));
        return toResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatusResponse update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found"));

        readStatus.updateLastReadAt();
        readStatusRepository.save(readStatus);

        return toResponse(readStatus);
    }

    @Override
    public void delete(UUID id) {
        if (!readStatusRepository.existsById(id)) {
            throw new NoSuchElementException("ReadStatus not found");
        }
        readStatusRepository.deleteById(id);
    }

    private ReadStatusResponse toResponse(ReadStatus readStatus) {
        return ReadStatusResponse.builder()
                .id(readStatus.getId())
                .userId(readStatus.getUserId())
                .channelId(readStatus.getChannelId())
                .lastReadAt(readStatus.getLastReadAt())
                .createdAt(readStatus.getCreatedAt())
                .updatedAt(readStatus.getUpdatedAt())
                .build();
    }
}