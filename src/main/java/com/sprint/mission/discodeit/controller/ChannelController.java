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
@RequestMapping("/api/channel")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/public/create", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPublic(@RequestBody CreatePublicChannelRequest publicChannelRequest) {
        ChannelResponse channelResponse = channelService.createPublicChannel(publicChannelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
    }

    @RequestMapping(value = "/private/create", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPrivate(@RequestBody CreatePrivateChannelRequest privateChannelRequest) {
        ChannelResponse channelResponse = channelService.createPrivateChannel(privateChannelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
    }

    @RequestMapping(value = "/public/update/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelResponse> updatePublic(
            @PathVariable UUID id,
            @RequestBody ChannelUpdateRequest channelUpdateRequest
    ) {
        ChannelResponse channelResponse = channelService.update(id, channelUpdateRequest);
        return ResponseEntity.ok(channelResponse);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        channelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponse>> findAllByUserId(@PathVariable UUID id) {
        List<ChannelResponse> channelResponseList = channelService.findAllByUserId(id);
        return ResponseEntity.ok(channelResponseList);
    }
}