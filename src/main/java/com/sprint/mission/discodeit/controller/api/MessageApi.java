package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.message.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "메시지 관리 API")
public interface MessageApi {

  @Operation(summary = "Message 생성 (파일 첨부 가능)")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  );

  @Operation(summary = "Channel의 Message 목록 조회")
  @GetMapping
  ResponseEntity<List<MessageDto>> getMessageList(@RequestParam UUID channelId);

  @Operation(summary = "Message 수정")
  @PatchMapping("/{messageId}")
  ResponseEntity<MessageDto> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request
  );

  @Operation(summary = "Message 삭제")
  @DeleteMapping("/{messageId}")
  ResponseEntity<String> delete(@PathVariable UUID messageId);
}