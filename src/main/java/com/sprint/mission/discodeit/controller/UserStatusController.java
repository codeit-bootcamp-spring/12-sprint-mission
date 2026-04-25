package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-statuses")
public class UserStatusController {

    private final UserStatusService userStatusService;

    public UserStatusController(UserStatusService userStatusService) {
        this.userStatusService = userStatusService;
    }

    // 상태 생성
    @RequestMapping(method = RequestMethod.POST)
    public UserStatus create(
            @RequestBody UserStatusCreateRequest request
    ){
        return userStatusService.create(request);
    }

    // 전체 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<UserStatus> findAll(){
        return userStatusService.findAll();
    }

    // 단건 조회
    @RequestMapping(value="/{userStatusId}", method = RequestMethod.GET)
    public UserStatus find(
            @PathVariable UUID userStatusId
    ){
        return userStatusService.find(userStatusId);
    }

    // 상태 수정
    @RequestMapping(value="/{userStatusId}", method = RequestMethod.PATCH)
    public UserStatus update(
            @PathVariable UUID userStatusId,
            @RequestBody UserStatusUpdateRequest request
    ){
        return userStatusService.update(userStatusId, request);
    }

    // userId 기준 온라인 상태 변경
    @RequestMapping(value="/users/{userId}", method = RequestMethod.PATCH)
    public UserStatus updateByUserId(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ){
        return userStatusService.updateByUserId(userId, request);
    }

    // 삭제
    @RequestMapping(value="/{userStatusId}", method = RequestMethod.DELETE)
    public void delete(
            @PathVariable UUID userStatusId
    ){
        userStatusService.delete(userStatusId);
    }
}