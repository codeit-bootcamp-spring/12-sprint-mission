package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "채널 관리 API")
public interface ChannelApi {

  @Operation(summary = "Public Channel 생성")
  @PostMapping("/public")
  ResponseEntity<Channel> createPublic(@RequestBody PublicChannelCreateRequest request);

  @Operation(summary = "Private Channel 생성")
  @PostMapping("/private")
  ResponseEntity<Channel> createPrivate(@RequestBody PrivateChannelCreateRequest request);

  @Operation(summary = "Channel 정보 수정")
  @PatchMapping("/{channelId}")
  ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId,
      @RequestBody ChannelUpdateRequest request
  );

  @Operation(summary = "Channel 삭제")
  @DeleteMapping("/{channelId}")
  ResponseEntity<Void> delete(@PathVariable UUID channelId);

  @Operation(summary = "User의 Channel 목록 조회")
  @GetMapping
  ResponseEntity<List<ChannelDto>> find(@RequestParam UUID userId);
}