package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<ChannelResponse> createPublic(@RequestBody CreatePublicChannelRequest publicChannelRequest) {
        ChannelResponse channelResponse = channelService.createPublicChannel(publicChannelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelResponse> createPrivate(@RequestBody CreatePrivateChannelRequest privateChannelRequest) {
        ChannelResponse channelResponse = channelService.createPrivateChannel(privateChannelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelResponse> updatePublic(
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequest channelUpdateRequest
    ) {
        ChannelResponse channelResponse = channelService.update(channelId, channelUpdateRequest);
        return ResponseEntity.ok(channelResponse);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelResponse>> findAllByUserId(@RequestParam("userId") UUID userId) {
        List<ChannelResponse> channelResponseList = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channelResponseList);
    }
}