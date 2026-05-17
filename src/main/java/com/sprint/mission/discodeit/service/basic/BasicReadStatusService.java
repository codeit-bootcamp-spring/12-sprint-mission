package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReadStatus create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User " + userId + " not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel " + channelId + " not found"));
        if (readStatusRepository.existsByUser_IdAndChannel_Id(userId, channelId)) {
            throw new IllegalArgumentException(
                    "ReadStatus with userId " + userId + " and channelId " + channelId + " already exists"
            );
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUser_Id(userId);
    }

    @Override
    @Transactional
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                        .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
        readStatus.update(request.newLastReadAt());
        return readStatus;
    }

    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                        .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
        readStatusRepository.delete(readStatus);
    }
}
