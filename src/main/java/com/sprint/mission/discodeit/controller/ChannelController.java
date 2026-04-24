package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPublicChannel(@ModelAttribute PublicChannelCreateRequest request) {
        try {
            Channel channel = channelService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(channel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPrivateChannel(@ModelAttribute PrivateChannelCreateRequest request) {
        try {
            Channel channel = channelService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(channel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @RequestMapping(value = "/{channelId}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<Channel> update(
            @PathVariable UUID channelId,
            @ModelAttribute PublicChannelUpdateRequest request) {
        try {
            Channel channelDto = channelService.update(channelId, request);
            return ResponseEntity.status(HttpStatus.OK).body(channelDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        try {
            channelService.delete(channelId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAll(@PathVariable UUID userId) {
        List<ChannelDto> channelDtos = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channelDtos);
    }
}
