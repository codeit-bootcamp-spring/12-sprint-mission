package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(
            @RequestPart UserCreateRequest user,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        BinaryContentCreateRequest binaryContent = null;
        if(file != null && !file.isEmpty()){
            binaryContent = new BinaryContentCreateRequest(
                    file.getName(),
                    file.getContentType(),
                    file.getBytes()
            );
        }
        User saveUser = userService.create(user, Optional.ofNullable(binaryContent));
        return ResponseEntity.status(HttpStatus.CREATED).body(saveUser);
    }

    @RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id,
            @RequestPart(required = false) UserUpdateRequest user,
            @RequestPart(required = false) MultipartFile file
    ) throws IOException {
        BinaryContentCreateRequest binaryContent = null;
        if(file != null && !file.isEmpty()){
            binaryContent = new BinaryContentCreateRequest(
                    file.getName(),
                    file.getContentType(),
                    file.getBytes()
            );
        }
        User updateUser = userService.update(id, user, Optional.ofNullable(binaryContent));
        return ResponseEntity.ok(updateUser);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id
    ) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @RequestMapping(path = "/{userId}/isonline", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Boolean> updateUserStatus(
            @PathVariable UUID userId
    ) {
        UserStatusUpdateRequest userStatus = new UserStatusUpdateRequest(Instant.now());
        Boolean isOnline = userStatusService.updateByUserId(userId, userStatus).isOnline();

        return ResponseEntity.status(HttpStatus.OK).body(isOnline);
    }

}
