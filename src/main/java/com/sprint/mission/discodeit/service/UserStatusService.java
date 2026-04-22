package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto create(UserStatusCreateRequestDto createDto);
    UserStatusResponseDto find(UUID Id);
    List<UserStatusResponseDto> findAll();
    UserStatusResponseDto update(UserStatusUpdateRequestDto updateDto);
    UserStatusResponseDto updateByUserId(UUID userId, LocalDateTime lastConnectedAt);
    void delete(UUID Id);
}
