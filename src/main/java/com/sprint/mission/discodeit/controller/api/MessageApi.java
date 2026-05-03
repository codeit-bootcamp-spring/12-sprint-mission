package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "메시지 관리 API")
public interface MessageApi {

    @Operation(summary = "Message 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message 생성 성공"),
            @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없음")
    })
    ResponseEntity<Message> create(
            @Parameter(description = "Message 생성 정보") MessageCreateRequest request,
            @Parameter(description = "Message 첨부 파일들") List<MultipartFile> attachments
    );

    @Operation(summary = "Message 내용 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없음")
    })
    ResponseEntity<Message> update(
            @Parameter(description = "수정할 Message ID") UUID messageId,
            @Parameter(description = "수정할 Message 내용") MessageUpdateRequest request
    );

    @Operation(summary = "Message 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Message 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 메시지가 존재하지 않음")
    })
    ResponseEntity<Void> delete(@Parameter(description = "삭제할 Message ID") UUID messageId);

    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
    ResponseEntity<List<Message>> findAllByChannelId(@Parameter(description = "조회할 Channel ID") UUID channelId);
}