package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

    @Operation(summary = "Message 생성")
    @ApiResponse(responseCode = "201", description = "Message 생성 성공")
    ResponseEntity<MessageResponse> createMessage(
        MessageCreateRequest request,
        List<MultipartFile> attachments
    );

    @Operation(summary = "Message 내용 수정")
    @ApiResponse(responseCode = "200", description = "Message 내용 수정 성공")
    ResponseEntity<MessageResponse> updateMessage(
        UUID messageId,
        MessageUpdateRequest request
    );

    @Operation(summary = "Message 삭제")
    @ApiResponse(responseCode = "204", description = "Message 삭제 성공")
    ResponseEntity<Void> deleteMessage(
        UUID messageId
    );

    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
    ResponseEntity<PageResponse<MessageResponse>> findMessagesByChannelId(
        UUID channelId, int page
    );
}
