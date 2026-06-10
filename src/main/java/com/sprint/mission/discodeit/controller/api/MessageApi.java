package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

  @Operation(summary = "Message 생성", operationId = "create_2")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found"))
      )
  })
  ResponseEntity<MessageDto> create(
      @Parameter(description = "Message 생성 정보")
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @Parameter(description = "Message 첨부 파일들")
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments);

  @Operation(summary = "Message 내용 수정", operationId = "update_2")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
      )
  })
  ResponseEntity<MessageDto> update(
      @Parameter(name = "messageId", description = "수정할 Message ID")
      @PathVariable UUID messageId,
      @RequestBody @Valid MessageUpdateRequest request);

  @Operation(summary = "Message 삭제", operationId = "delete_1")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(name = "messageId", description = "삭제할 Message ID")
      @PathVariable UUID messageId);

  @Operation(summary = "Channel의 Message 목록 조회", operationId = "findAllByChannelId")
  @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
  ResponseEntity<PageResponse<MessageDto>> findByChannelId(
      @Parameter(name = "channelId", description = "조회할 Channel ID")
      @RequestParam UUID channelId,
      @Parameter(name = "cursor", description = "페이징 커서 정보")
      @RequestParam(name = "cursor", required = false) Instant cursor,
      @Parameter(name = "pageable", description = "페이징 정보")
      @PageableDefault(size = 50,
          sort = "createdAt",
          direction = Direction.DESC
      ) Pageable pageable);

}
