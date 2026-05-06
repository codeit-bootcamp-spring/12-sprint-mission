package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.entity.BinaryContent;
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

@Tag(name = "BinaryContent", description = "Attachment API")
public interface BinaryContentApi {

  @Operation(summary = "Find Attachment")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Attachment Search Successful",
          content = @Content(schema = @Schema(implementation = BinaryContent.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Unable to find attachment",
          content = @Content(examples = @ExampleObject(value = "BinaryContent with id {binaryContentId} not found"))
      )
  })
  ResponseEntity<BinaryContent> find(
      @Parameter(description = "attachment file ID") UUID binaryContentId
  );

  @Operation(summary = "Multiple attachment file search")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Attachment list Search Successful",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class)))
      )
  })
  ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @Parameter(description = "attachment file ID list") List<UUID> binaryContentIds
  );
} 