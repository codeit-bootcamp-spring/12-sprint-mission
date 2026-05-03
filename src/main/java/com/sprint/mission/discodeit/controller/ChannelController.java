package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;


  // Create Public Channel
  @Operation(summary = "Create a new public channel")
  @PostMapping("/public")
  public ResponseEntity<ChannelDto> createPublic
  (@RequestBody PublicChannelCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.create(request));
  }


  // Create Private Channel
  @Operation(summary = "Create a new private channel")
  @ApiResponse(responseCode = "201", description = "생성 성공")
  @PostMapping("/private")
  public ResponseEntity<ChannelDto> createPrivate(
      @RequestBody PrivateChannelCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.create(request));
  }
      
  // Find Channel by ID
  @Operation(summary = "Find a channel by ID")
  @GetMapping("/{channelId}")
  public ResponseEntity<ChannelDto> find(@PathVariable UUID channelId) {
    return ResponseEntity.ok(channelService.find(channelId));
  }

  // Revise Public Channel
  @Operation(summary = "Update a channel")
  @PatchMapping("/{channelId}")
  public ResponseEntity<Channel> update(
      @PathVariable UUID channelId,
      @RequestBody ChannelUpdateRequest request) {
    return ResponseEntity.ok(channelService.update(channelId, request));
  }

  // Delete Channel
  @Operation(summary = "Delete a channel")
  @DeleteMapping("/{channelid}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  // Find Channel by User
  @Operation(summary = "Find channels by user ID")
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }
}