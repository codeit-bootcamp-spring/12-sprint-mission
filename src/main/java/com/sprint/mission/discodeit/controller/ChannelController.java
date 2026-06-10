package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<ChannelResponse> createPublic(
            @Valid @RequestBody CreatePublicChannelRequest publicChannelRequest) {
        log.info("Public channel create API requested. name={}", publicChannelRequest.name());

        ChannelResponse channelResponse = channelService.createPublicChannel(publicChannelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelResponse> createPrivate(
            @Valid @RequestBody CreatePrivateChannelRequest privateChannelRequest) {
        log.info(
                "Private channel create API requested. participantCount={}",
                privateChannelRequest.participantIds().size()
        );

        ChannelResponse channelResponse = channelService.createPrivateChannel(privateChannelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelResponse> updatePublic(
            @PathVariable UUID channelId,
            @Valid @RequestBody ChannelUpdateRequest channelUpdateRequest
    ) {
        log.info("Channel update API requested. channelId={}", channelId);

        ChannelResponse channelResponse = channelService.update(channelId, channelUpdateRequest);
        return ResponseEntity.ok(channelResponse);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        log.warn("Channel delete API requested. channelId={}", channelId);

        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelResponse>> findAllByUserId(@RequestParam("userId") UUID userId) {
        List<ChannelResponse> channelResponseList = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channelResponseList);
    }
}