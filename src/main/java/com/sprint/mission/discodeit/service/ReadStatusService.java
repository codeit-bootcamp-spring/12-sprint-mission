package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDTO;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadStatusService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;

    public ReadStatus create(ReadStatusCreateDTO readStatusCreateDTO) {
        if (!channelRepository.existsById(readStatusCreateDTO.getChannelId())) {
            throw new NoSuchElementException("Channel not found with id " + readStatusCreateDTO.getChannelId());
        }
        if (!userRepository.existsById(readStatusCreateDTO.getUserId())) {
            throw new NoSuchElementException("User not found with id " + readStatusCreateDTO.getUserId());
        }
        if (readStatusRepository.findByUserId(readStatusCreateDTO.getUserId())
                .equals(readStatusRepository.findByChannelId(readStatusCreateDTO.getChannelId()))) {
            throw new IllegalArgumentException("ReadStatus with Channel " + readStatusCreateDTO.getChannelId() + " and User " + readStatusCreateDTO.getUserId() + " already exists");
        }
        return new ReadStatus(readStatusCreateDTO.getUserId(), readStatusCreateDTO.getChannelId());
    }

    public ReadStatus find(UUID id) {
        return readStatusRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("ReadStatus not found with id " + id)
        );
    }

    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findByUserId(userId);
    }

    public ReadStatus update(ReadStatusUpdateDTO readStatusUpdateDTO) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusUpdateDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + readStatusUpdateDTO.getId()));
        readStatus.update(readStatusUpdateDTO.getCheckedAt());
        return readStatusRepository.save(readStatus);
    }

    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }

}
