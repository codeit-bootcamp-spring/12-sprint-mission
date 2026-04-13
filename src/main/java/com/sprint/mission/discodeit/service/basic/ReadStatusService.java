package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;
import com.sprint.mission.discodeit.data.request.channel.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.data.request.channel.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.channel.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ReadStatus create(ReadStatusCreateRequest request) {
        if (request.userId() == null) {
            throw new IllegalArgumentException("유저 아이디 없음");
        }

        if (request.channelId() == null) {
            throw new IllegalArgumentException("채널 아이디 없음");
        }

        if (readStatusRepository.findAllByUserId(request.userId()).stream().anyMatch(readStatus -> readStatus.getChannelId().equals(request.channelId()))) {
            throw new IllegalArgumentException("이미 ReadStatus에 존재하는 userId / channelId");
        }

        if (userRepository.findById(request.userId()).isEmpty()) {
            throw new IllegalArgumentException("저장소에 userId 없음");
        }

        if (channelRepository.findById(request.channelId()).isEmpty()) {
            throw new IllegalArgumentException("저장소에 channelId 없음");
        }

        return readStatusRepository.save(new ReadStatus(request.userId(), request.channelId()));
    }

    public ReadStatus find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id).orElse(null);

        if (readStatus == null) {
            throw new IllegalArgumentException("ReadStatus 없음");
        }

        return readStatus;
    }

    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    public ReadStatus update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(id).orElse(null);

        if (readStatus == null) {
            throw new IllegalArgumentException("ReadStatus 없음");
        }

        readStatus.update();

        return readStatusRepository.save(readStatus);
    }

    public ReadStatus delete(UUID id) {
        return readStatusRepository.deleteById(id);
    }
}
