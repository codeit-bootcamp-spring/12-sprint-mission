package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    // [ ] 사용자를 등록할 수 있다.
    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(
            @RequestPart UserCreateRequest user,
            @RequestPart BinaryContentCreateRequest binaryContent
    ) {
        Optional<BinaryContentCreateRequest> content = Optional.ofNullable(binaryContent);
        User saveUser = userService.create(user, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveUser);
    }

    // [ ] 사용자 정보를 수정할 수 있다.
    @RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id,
            @RequestPart UserUpdateRequest user,
            @RequestPart BinaryContentCreateRequest binaryContent
    ) {
        Optional<BinaryContentCreateRequest> content = Optional.ofNullable(binaryContent);
        User updateUser = userService.update(id, user, content);
        return ResponseEntity.ok(updateUser);
    }

    // [ ] 사용자를 삭제할 수 있다.
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // [ ] 모든 사용자를 조회할 수 있다.
    @RequestMapping(path = "/" , method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll(){
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
    // [ ] 사용자의 온라인 상태를 업데이트할 수 있다.
    @RequestMapping(path = "/{userId}/isonline" , method = {RequestMethod.PUT,RequestMethod.PATCH})
    public ResponseEntity<Boolean> updateUserStatus (
            @PathVariable UUID userId,
            @RequestPart UserStatusUpdateRequest userStatus
            ) {
        Boolean isOnline = userStatusService.updateByUserId(userId,userStatus).isOnline();
        return ResponseEntity.status(HttpStatus.OK).body(isOnline);
    }

    // [ ]  웹 API의 예외를 전역으로 처리하세요.
}
