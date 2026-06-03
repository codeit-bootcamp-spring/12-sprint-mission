package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
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
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @PostMapping("/public")
  @Override
  public ResponseEntity<ChannelDto> createPublic(
      @RequestBody @Valid PublicChannelCreateRequest request) {
    log.debug("Public Channel 생성 요청: request={}", request);
    ChannelDto createdChannel = channelService.create(request);
    log.info("Public Channel 생성 응답: channelId={}, name={}", createdChannel.id(),
        createdChannel.name());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @PostMapping("/private")
  @Override
  public ResponseEntity<ChannelDto> createPrivate(
      @RequestBody @Valid PrivateChannelCreateRequest request) {
    log.debug("Private Channel 생성 요청: request={}", request);
    ChannelDto createdChannel = channelService.create(request);
    log.info("Private Channel 생성 응답: channelId={}, name={}", createdChannel.id(),
        createdChannel.name());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @PatchMapping("/{channelId}")
  @Override
  public ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId,
      @RequestBody @Valid PublicChannelUpdateRequest request) {
    log.debug("Public Channel 수정 요청: channelId={}, request={}", channelId, request);
    ChannelDto updatedChannel = channelService.update(channelId, request);
    log.info("Public Channel 수정 응답: channelId={}, name={}", updatedChannel.id(),
        updatedChannel.name());
    return ResponseEntity.ok(updatedChannel);
  }

  @DeleteMapping("/{channelId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    log.debug("Channel 삭제 요청: channelId={}", channelId);
    channelService.delete(channelId);
    log.info("Channel 삭제 응답: channelId={}", channelId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping()
  @Override
  public ResponseEntity<List<ChannelDto>> find(@RequestParam(value = "userId") UUID userId) {
    log.debug("User의 Channel 목록 조회 요청: userId={}", userId);
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    log.info("User의 Channel 목록 조회 응답: userId={}, foundCount={}", userId, channels.size());
    return ResponseEntity.ok(channels);
  }
}
