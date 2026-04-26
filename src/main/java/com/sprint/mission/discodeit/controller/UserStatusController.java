package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/userStatuses")
public class UserStatusController {
    private final UserStatusService userStatusService;

    public UserStatusController(UserStatusService userStatusService) {
        this.userStatusService = userStatusService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public UserStatus createUserStatus(@RequestBody UserStatusCreateRequest request){
        return userStatusService.create(request);
    }

    @RequestMapping(value = "/{userStatusId}", method = RequestMethod.GET)
    public UserStatus findUserStatus(@PathVariable UUID userStatusId){
        return userStatusService.find(userStatusId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserStatus> findAllUserStatus(){
        return userStatusService.findAll();
    }

    @RequestMapping(value = "/{userStatusId}", method = RequestMethod.PATCH)
    public UserStatus updateUserStatus(
            @PathVariable UUID userStatusId,
            @RequestBody UserStatusUpdateRequest request){
        return userStatusService.update(userStatusId, request);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public UserStatus updateByUserIdUserStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request){
        return userStatusService.updateByUserId(userId, request);
    }

    @RequestMapping(value = "/{userStatusId}", method = RequestMethod.DELETE)
    public void deleteUserStatus(@PathVariable UUID userStatusId){
        userStatusService.delete(userStatusId);
    }
}
