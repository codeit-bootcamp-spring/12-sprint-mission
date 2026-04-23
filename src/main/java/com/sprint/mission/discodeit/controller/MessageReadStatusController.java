package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/readstatuses")
@RequiredArgsConstructor
public class MessageReadStatusController {
    private final ReadStatusService readStatusService;

    // [ ] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
    @RequestMapping(path = "/",method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest status){
        ReadStatus savedReadStatus = readStatusService.create(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReadStatus);
    }

    //[ ] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
    @RequestMapping(path ="/{id}", method = {RequestMethod.PUT , RequestMethod.PATCH})
    public ResponseEntity<ReadStatus> update(
            @PathVariable UUID id,
            @RequestBody ReadStatusUpdateRequest status){
        ReadStatus updateStatus = readStatusService.update(id,status);
        return ResponseEntity.status(HttpStatus.OK).body(updateStatus);
    }

    //[ ] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
    @RequestMapping(path = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findByUserId(@PathVariable UUID userId){
        List<ReadStatus> list = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
