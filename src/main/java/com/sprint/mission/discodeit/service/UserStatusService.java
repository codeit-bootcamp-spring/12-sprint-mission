package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateDTO;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public UserStatus create(UserStatusCreateDTO userStatusCreateDTO) {
        if (!userRepository.existsById(userStatusCreateDTO.getUserId())) {
            throw new NoSuchElementException("User not found with id " + userStatusCreateDTO.getUserId());
        }
        if (userStatusRepository.existByUserId(userStatusCreateDTO.getUserId())) {
            throw new IllegalArgumentException("UserStatus with user id " + userStatusCreateDTO.getUserId() + " already exists");
        }
        return userStatusRepository.save(new UserStatus(userStatusCreateDTO.getUserId()));
    }

    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
    }

    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    public UserStatus update(UserStatusUpdateDTO userStatusUpdateDTO) {
        UserStatus userStatus = userStatusRepository.findById(userStatusUpdateDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userStatusUpdateDTO.getId() + " not found"));
        userStatus.update(userStatusUpdateDTO.getAccessedAt());
        return userStatusRepository.save(userStatus);
    }

    public UserStatus updateByUserId(UserStatusUpdateDTO userStatusUpdateDTO) {
        UserStatus userStatus = userStatusRepository.findByUserId(userStatusUpdateDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus with user id " + userStatusUpdateDTO.getId() + " not found"));
        userStatus.update(userStatusUpdateDTO.getAccessedAt());
        return userStatusRepository.save(userStatus);
    }

    public void deleteById(UUID id) {
        userStatusRepository.deleteById(id);
    }
}
