package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 관리 API")
public interface BinaryContentApi {

    @Operation(summary = "첨부 파일 조회")
    @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공")
    ResponseEntity<BinaryContent> find(@Parameter(description = "조회할 첨부 파일 ID") UUID binaryContentId);

    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
    ResponseEntity<List<BinaryContent>> findAllByIdIn(@Parameter(description = "조회할 첨부 파일 ID 목록") List<UUID> binaryContentIds);



}