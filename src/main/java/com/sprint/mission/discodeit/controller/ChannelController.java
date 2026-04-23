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

    // [ ] 공개 채널을 생성할 수 있다.
    @RequestMapping(path = "/public", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPublic(
            @RequestBody PublicChannelCreateRequest channel){
        Channel saveChannel = channelService.create(channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveChannel);
    }

    // [ ] 비공개 채널을 생성할 수 있다.
    @RequestMapping(path = "/private", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPrivate(
            @RequestBody PrivateChannelCreateRequest channel){
        Channel saveChannel = channelService.create(channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveChannel);
    }

    // [ ] 공개 채널의 정보를 수정할 수 있다.
    @RequestMapping(path = "/{id}", method = {RequestMethod.PUT , RequestMethod.PATCH})
    public ResponseEntity<Channel> update(
            @PathVariable UUID id,
            @RequestBody PublicChannelUpdateRequest channel){
        Channel updateChannel = channelService.update(id, channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateChannel);
    }


    // [ ] 채널을 삭제할 수 있다.
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        channelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // [ ] 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUserId (@PathVariable UUID id){
        List<ChannelDto> list = channelService.findAllByUserId(id);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
