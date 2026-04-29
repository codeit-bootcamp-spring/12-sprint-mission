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

    @RequestMapping(value = "/createPublic", method = RequestMethod.POST)
    public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @RequestMapping(value = "/createPrivate", method = RequestMethod.POST)
    public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PATCH)
    public ResponseEntity<Channel> update(
            @RequestParam UUID channelId,
            @RequestBody PublicChannelUpdateRequest request) {
        Channel channel = channelService.update(channelId, request);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
}
