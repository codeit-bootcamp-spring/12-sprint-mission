package com.sprint.mission.discodeit.service.Impl;

import com.sprint.mission.discodeit.dto.status.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.status.UserStatusResponse;
import com.sprint.mission.discodeit.dto.status.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        userRepository.findById(request.userId())
                .orElseThrow(()-> new NoSuchElementException("존재하지 않는 유저입니다."));

        if (userStatusRepository.findByUserId(request.userId()).isPresent()) {
            throw new IllegalStateException("이미 해당 유저의 상태정보가 존재합니다.");
        }

        UserStatus status = new UserStatus(request.userId());
        UserStatus savedStatus = userStatusRepository.save(status);

        return convertToResponse(savedStatus);
    }

    public UserStatusResponse convertToResponse(UserStatus status) {
        return new UserStatusResponse(
                status.getId(),
                status.getUserId()
        );
    }

    @Override
    public UserStatusResponse find(UUID id) {
        UserStatus status = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 유저상태가 존재하지 않습니다."));
        return convertToResponse(status);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        List<UserStatus> statuses = userStatusRepository.findAll();
        if(statuses.isEmpty()) return Collections.emptyList();


        return statuses.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public UserStatusResponse update(UserStatusUpdateRequest request) {
        UserStatus status = userStatusRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("상태 정보가 존재하지 않습니다"));
        status.updateActiveTime();
        return convertToResponse(status);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저에 대한 상태가 존재하지 않습니다"));
        status.updateActiveTime();
        return convertToResponse(status);
    }

    @Override
    public void delete(UUID id) {
        if (userStatusRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("상태 정보가 존재하지 않습니다");
        }
        userStatusRepository.delete(id);
    }
}
