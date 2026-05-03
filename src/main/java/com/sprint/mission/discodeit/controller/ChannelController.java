package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  // POST /api/channels/public
  @PostMapping("/public")
  public ResponseEntity<Channel> createPublic(@RequestBody PublicChannelCreateRequest request) {
    Channel channel = channelService.createPublicChannel(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(channel);
  }

  // POST /api/channels/private
  @PostMapping("/private")
  public ResponseEntity<Channel> createPrivate(@RequestBody PrivateChannelCreateRequest request) {
    Channel channel = channelService.createPrivateChannel(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(channel);
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId,
      @ModelAttribute ChannelUpdateRequest request
  ) {
    ChannelUpdateRequest serviceRequest = new ChannelUpdateRequest(
        channelId,
        request.newName(),
        request.newDescription()
    );

    ChannelDto channel = channelService.update(serviceRequest);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channel);
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> find(@RequestParam UUID userId) {
    List<ChannelDto> channelDtos = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(channelDtos);
  }
}
