package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public BasicUserStatusService(
            @Qualifier("jCFUserStatusRepository") UserStatusRepository userStatusRepository,
            @Qualifier("jCFUserRepository") UserRepository userRepository
    ) {
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserStatusResponseDto create(UserStatusCreateRequestDto createDto) {
        if (!userRepository.existsById(createDto.userId())) {
            throw new NoSuchElementException("User not found with id " + createDto.userId());
        }

        if (userStatusRepository.existsByUserId(createDto.userId())) {
            throw new IllegalArgumentException("UserStatus already exists for user " + createDto.userId());
        }

        UserStatus userStatus = new UserStatus(createDto.userId());
        userStatusRepository.save(userStatus);

        return UserStatusResponseDto.from(userStatus);
    }

    @Override
    public UserStatusResponseDto find(UUID Id) {
        UserStatus userStatus = userStatusRepository.findById(Id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id " + Id));
        return UserStatusResponseDto.from(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAll() {
        List<UserStatus> userStatuses = userStatusRepository.findAll();
        List<UserStatusResponseDto> dtos = new ArrayList<>();
        for (UserStatus userStatus : userStatuses) {
            dtos.add(UserStatusResponseDto.from(userStatus));
        }
       return dtos;
    }

    @Override
    public UserStatusResponseDto update(UserStatusUpdateRequestDto updateDto) {
        UserStatus userStatus = userStatusRepository.findById(updateDto.id())
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));

        userStatus.updateConnection(updateDto.lastConnectedAt());
        userStatusRepository.save(userStatus);

        return UserStatusResponseDto.from(userStatus);
    }

    @Override
    public UserStatusResponseDto updateByUserId(UUID userId, LocalDateTime lastConnectedAt) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));

        userStatus.updateConnection(lastConnectedAt);
        userStatusRepository.save(userStatus);

        return UserStatusResponseDto.from(userStatus);
    }

    @Override
    public void delete(UUID Id) {
        userStatusRepository.deleteById(Id);
    }
}
