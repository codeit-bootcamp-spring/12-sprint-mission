package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        User user = userRepository.findById(request.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        Channel channel = channelRepository.findById(request.getChannelId());
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        ReadStatus existing = readStatusRepository.findByUserIdAndChannelId(
                request.getUserId(),
                request.getChannelId()
        );

        if (existing != null) {
            throw new IllegalStateException("이미 해당 user와 channel에 대한 ReadStatus가 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(
                request.getUserId(),
                request.getChannelId()
        );

        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        return readStatusRepository.findById(id);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus target = readStatusRepository.findById(id);

        if (target == null) {
            throw new IllegalArgumentException("ReadStatus가 존재하지 않습니다.");
        }

        target.updateLastRead();
        readStatusRepository.save(target);
        return target;
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.delete(id);
    }
}