package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public User create(
            @RequestBody UserCreateRequest request
    ){
        return userService.create(
                request,
                Optional.empty()
        );
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> findAll(){
        return userService.findAll();
    }

    @RequestMapping(
            value="/{userId}",
            method=RequestMethod.GET
    )
    public UserDto findById(
            @PathVariable UUID userId
    ){
        return userService.find(userId);
    }

    @RequestMapping(
            value="/{userId}",
            method=RequestMethod.PATCH
    )
    public User update(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequest request
    ){
        return userService.update(
                userId,
                request,
                Optional.empty()
        );
    }

    @RequestMapping(
            value="/{userId}",
            method=RequestMethod.DELETE
    )
    public void delete(
            @PathVariable UUID userId
    ){
        userService.delete(userId);
    }

    @RequestMapping(value = "/api/user/findAll", method = RequestMethod.GET)

    public ResponseEntity<List<UserDto>> findAllForStaticPage() {

        return ResponseEntity.ok(userService.findAll());

    }
}