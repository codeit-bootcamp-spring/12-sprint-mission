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

import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        ChannelDto savedChannel = channelService.find(channel.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedChannel);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        ChannelDto savedChannel = channelService.find(channel.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedChannel);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto> updatePublicChannel(
            @PathVariable UUID id,
            @RequestBody PublicChannelUpdateRequest request) {
        Channel channel = channelService.update(id, request);
        ChannelDto updatedChannel = channelService.find(channel.getId());
        return ResponseEntity.ok(updatedChannel);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ChannelDto> deleteChannel(@PathVariable UUID id) {
        ChannelDto channel = channelService.find(id);
        channelService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }
}
