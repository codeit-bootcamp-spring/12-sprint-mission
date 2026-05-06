package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "Message ReadStatus API")
public interface ReadStatusApi {

  @Operation(summary = "Create Message ReadStatus")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Message ReadStatus Created Successfully",
          content = @Content(schema = @Schema(implementation = ReadStatus.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Channel | User with id {channelId | userId} not found",
          content = @Content(examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "ReadStatus with userId {userId} and channelId {channelId} already exists",
          content = @Content(examples = @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists"))
      )
  })
  ResponseEntity<ReadStatus> create(
      @Parameter(description = "Message ReadStatus info") ReadStatusCreateRequest request
  );

  @Operation(summary = "Edit Message ReadStatus ")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message ReadStatus Edited Successfully",
          content = @Content(schema = @Schema(implementation = ReadStatus.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "ReadStatus with id {readStatusId} not found",
          content = @Content(examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found"))
      )
  })
  ResponseEntity<ReadStatus> update(
      @Parameter(description = "Edited Read Status ID") UUID readStatusId,
      @Parameter(description = "Edited Read Status Info") ReadStatusUpdateRequest request
  );

  @Operation(summary = "Find User's Message ReadStatus List")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message ReadStatus List Search Successfully",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class)))
      )
  })
  ResponseEntity<List<ReadStatus>> findAllByUserId(
      @Parameter(description = "Search User ID") UUID userId
  );
} 