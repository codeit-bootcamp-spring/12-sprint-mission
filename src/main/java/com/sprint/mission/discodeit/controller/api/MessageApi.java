package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

  @Operation(summary = "Create Message")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Message Created Successfullly",
          content = @Content(schema = @Schema(implementation = Message.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Channel | Author with id {channelId | authorId} not found",
          content = @Content(examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found"))
      ),
  })
  ResponseEntity<MessageDto> create(
      @Parameter(
          description = "Message Creation Info",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) MessageCreateRequest messageCreateRequest,
      @Parameter(
          description = "Message Attachments",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ) List<MultipartFile> attachments
  );

  @Operation(summary = "Edit Message Content")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message Edited Successfully",
          content = @Content(schema = @Schema(implementation = Message.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Unable to find Message",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
      ),
  })
  ResponseEntity<MessageDto> update(
      @Parameter(description = "Edit Message ID") UUID messageId,
      @Parameter(description = "Edit Message content") MessageUpdateRequest request
  );

  @Operation(summary = "Message delete")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "Message Deleted Successfully"
      ),
      @ApiResponse(
          responseCode = "404", description = "Message with id {messageId} not found",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
      ),
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "Delete Message ID") UUID messageId
  );

  @Operation(summary = "ind Message List in Channel")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message List Search Successful ",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class)))
      )
  })
  ResponseEntity<List<MessageDto>> findAllByChannelId(
      @Parameter(description = "Find Channel ID") UUID channelId
  );
} 