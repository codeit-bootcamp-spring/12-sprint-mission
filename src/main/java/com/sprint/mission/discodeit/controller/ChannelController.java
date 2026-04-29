package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(path = "/createPublic", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        Channel publicCreatedChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicCreatedChannel);
    }

    // 비공개 채널 생성
    @RequestMapping(path = "/createPrivate", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        Channel privateCreateChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(privateCreateChannel);
    }

    // 공개 채널 정보 수정
    @RequestMapping(path = "/updatePrivate/{channelId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Channel> updatePrivateChannel(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {
        Channel updatePublicChannel = channelService.update(channelId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatePublicChannel);
    }

    // 채널 삭제
    @RequestMapping(path = "/delete/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 전체 채널 목록 조회
    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
}
