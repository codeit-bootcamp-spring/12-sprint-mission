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
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(path = "/public", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPublic(
            @RequestBody PublicChannelCreateRequest channel
    ){
        Channel saveChannel = channelService.create(channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveChannel);
    }

    @RequestMapping(path = "/private", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPrivate(
            @RequestBody PrivateChannelCreateRequest channel
    ){
        Channel saveChannel = channelService.create(channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveChannel);
    }

    @RequestMapping(path = "/{id}", method = {RequestMethod.PUT , RequestMethod.PATCH})
    public ResponseEntity<Channel> update(
            @PathVariable UUID id,
            @RequestBody PublicChannelUpdateRequest channel){
        Channel updateChannel = channelService.update(id, channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateChannel);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ){
        channelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUserId (
            @PathVariable UUID userId
    ){
        List<ChannelDto> list = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
