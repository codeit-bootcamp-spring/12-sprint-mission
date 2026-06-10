package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelPrivateCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelPublicCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

    @Operation(summary = "Public Channel 생성")
    @ApiResponse(responseCode = "201", description = "Public Channel 생성 성공")
    ResponseEntity<ChannelResponse> createPublicChannel(
        ChannelPublicCreateRequest request
    );

    @Operation(summary = "Private Channel 생성")
    @ApiResponse(responseCode = "201", description = "Private Channel 생성 성공")
    ResponseEntity<ChannelResponse> createPrivateChannel(
        ChannelPrivateCreateRequest request
    );

    @Operation(summary = "Channel 정보 수정")
    @ApiResponse(responseCode = "200", description = "Channel 정보 수정 성공")
    ResponseEntity<ChannelResponse> updateChannel(
        UUID channelId,
        ChannelUpdateRequest request
    );

    @Operation(summary = "Channel 삭제")
    @ApiResponse(responseCode = "204", description = "Channel 삭제 성공")
    ResponseEntity<Void> deleteChannel(
        UUID channelId
    );

    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
    ResponseEntity<List<ChannelResponse>> findAllByUserId(
        UUID userId
    );
}
