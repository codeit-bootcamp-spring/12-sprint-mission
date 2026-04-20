package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.UUID;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;
    private final ChannelService channelService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create(
            @ModelAttribute ReadStatusCreateRequest request) {

        ReadStatus readStatus = readStatusService.create(request);

        if (readStatus == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @RequestMapping(value = "/{readStatusId}", method = {RequestMethod.PATCH, RequestMethod.PUT })
    public ResponseEntity<ReadStatus> update(
            @PathVariable UUID readStatusId,
            @ModelAttribute ReadStatusUpdateRequest request) {

        if (readStatusService.find(readStatusId) == null) {
            return ResponseEntity.notFound().build();
        }

        ReadStatus readStatus = readStatusService.update(readStatusId, request);

        return ResponseEntity.status(HttpStatus.OK).body(readStatus);
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAll(@PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(readStatusService.findAllByUserId(userId));
    }
}
