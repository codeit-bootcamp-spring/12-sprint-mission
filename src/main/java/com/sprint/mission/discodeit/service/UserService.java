package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// 서비스 인터페이스
// 여기선 DTO(UI로부터 받아올 요청과 응답) 활용
public interface UserService {
    User create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest);
    UserDto find(UUID userId);
    List<UserDto> findAll();
    User update(UUID userId,
                UserUpdateRequest userUpdateRequest,
                Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);

    void delete(UUID userId);
}
