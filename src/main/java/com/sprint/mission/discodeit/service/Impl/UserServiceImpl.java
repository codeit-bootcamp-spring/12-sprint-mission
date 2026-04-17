package com.sprint.mission.discodeit.service.Impl;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email()))
            throw new IllegalStateException("이미 가입된 email 입니다.");

        if (userRepository.existsByNickname(request.nickname()))
            throw new IllegalStateException("이미 사용중인 닉네임 입니다.");

        BinaryContent profile = null;
        if (request.profileUrl() != null) {
            profile = new BinaryContent(request.profileFileName(), request.profileUrl());
            binaryContentRepository.save(profile);
        }

        User user = new User(profile, request.name(), request.email(), request.nickname(), request.password());
        UserStatus status = new UserStatus(user.getId());

        userRepository.save(user);
        userStatusRepository.save(status);

        return convertToResponse(user, status);
    }

    public UserResponse convertToResponse(User user, UserStatus status) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileId(),
                status.isOnline()
        );
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("유저를 찾을 수 없습니다."));
        UserStatus status = userStatusRepository.findByUserId(id)
                .orElseThrow(()-> new NoSuchElementException("상태 정보를 찾을 수 없습니다."));
        return convertToResponse(user, status);
    }

    @Override
    public List<UserResponse> findAll() {
        List<UserStatus> statuses = userStatusRepository.findAll();
        if(statuses.isEmpty()) return Collections.emptyList();

        Map<UUID, UserStatus> statusMap = statuses.stream()
                .collect(Collectors.toMap(UserStatus::getUserId,status -> status));

        return userRepository.findAll().stream()
                .map(user ->{
                    UserStatus status = statusMap.get(user.getId());
                    return convertToResponse(user,status);
                })
                .toList();
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(()-> new NoSuchElementException("해당 User가 존재하지 않습니다."));
        user.update(
                request.name(),
                request.profileId(),
                request.password()
        );

        userRepository.save(user);
        return findById(request.id());
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        UserStatus status = userStatusRepository.findByUserId(id)
                .orElseThrow(()-> new NoSuchElementException("해당 User Status를 찾을 수 없습니다."));
        userStatusRepository.delete(status.getId());

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userRepository.deleteById(id);
    }
}











