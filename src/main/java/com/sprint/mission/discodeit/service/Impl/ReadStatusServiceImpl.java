package com.sprint.mission.discodeit.service.Impl;

import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.ReadStatuscreateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("readStatusService")
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(ReadStatuscreateRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("존재하지 않는 유저입니다.");
        }
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("존재하지 않는 채널입니다.");
        }

        ReadStatus isExist = readStatusRepository.findByChannelIdAndUserId(request.channelId(),request.userId());

        if(isExist !=null){
            throw new IllegalStateException("이미 해당 채널에 참여중인 유저입니다.");
        }

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        ReadStatus savedReadStatus = readStatusRepository.save(readStatus);
        return convertToResponse(savedReadStatus);
    }

    public ReadStatusResponse convertToResponse(ReadStatus readStatus) {
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt()
        );
    }

    @Override
    public ReadStatusResponse find(UUID id) {
        ReadStatus status = readStatusRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("해당 유저가 존재하지 않습니다.")); // optional 사용? ReadStatus 사용?
        return convertToResponse(status);
    }

    @Override
    public List<ReadStatusResponse> finAllByUserId(UUID userId) {
        List<ReadStatus> statuses = readStatusRepository.findByUserId(userId);

        return statuses.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        ReadStatus status = readStatusRepository.findById(request.id())
                .orElseThrow(()-> new NoSuchElementException("참여 상태를 찾을 수 없습니다."));

        status.updateReadTime();
        return convertToResponse(status);
    }

    @Override
    public void delete(UUID id) {
        ReadStatus status = readStatusRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("참여 상태를 찾을 수 없습니다."));

        readStatusRepository.delete(id);
    }
}
