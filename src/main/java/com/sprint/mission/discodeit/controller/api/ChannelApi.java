package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
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

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

  @Operation(summary = "Create Public Channel ")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Public Channel Created Successfully",
          content = @Content(schema = @Schema(implementation = Channel.class))
      )
  })
  ResponseEntity<ChannelDto> create(
      @Parameter(description = "Public Channel Info") PublicChannelCreateRequest request
  );

  @Operation(summary = "Create Private Channel")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Private Channel Created Successfully",
          content = @Content(schema = @Schema(implementation = Channel.class))
      )
  })
  ResponseEntity<ChannelDto> create(
      @Parameter(description = "Private Channel Info") PrivateChannelCreateRequest request
  );

  @Operation(summary = "Edit Channel Info")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Channel Info Updated",
          content = @Content(schema = @Schema(implementation = Channel.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Unable to find the Channel",
          content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "Private channel cannot be updated",
          content = @Content(examples = @ExampleObject(value = "Private channel cannot be updated"))
      )
  })
  ResponseEntity<Channel> update(
      @Parameter(description = "Edit Channel ID") UUID channelId,
      @Parameter(description = "Channel Edit info") PublicChannelUpdateRequest request
  );

  @Operation(summary = "Channel Delete")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "Channel Deleted Successfully"
      ),
      @ApiResponse(
          responseCode = "404", description = "Unable to find the Channel",
          content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found"))
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = " Delete Channel ID") UUID channelId
  );

  @Operation(summary = "Find User included Channel list")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Channel List Search Successful",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))
      )
  })
  ResponseEntity<List<ChannelDto>> findAll(
      @Parameter(description = "Search User ID") UUID userId
  );
} 