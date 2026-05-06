package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "읽음 상태 관리 API")
public interface ReadStatusApi {

    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "데이터 없음")
    })
    ResponseEntity<ReadStatus> create(@Parameter(description = "Message 읽음 상태 생성 정보") ReadStatusCreateRequest request);

    @Operation(summary = "Message 읽음 상태 수정")
    ResponseEntity<ReadStatus> update(@Parameter(description = "수정할 읽음 상태 ID") UUID readStatusId,
                                      @Parameter(description = "수정할 읽음 상태 정보") ReadStatusUpdateRequest request);

    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    ResponseEntity<List<ReadStatus>> findAllByUserId(@Parameter(description = "조회할 User ID") UUID userId);
}
