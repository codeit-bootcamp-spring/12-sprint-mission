package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User API")
public interface UserApi {

    @Operation(summary = "User 등록")
    @ApiResponse(responseCode = "201", description = "User 등록 성공")
    ResponseEntity<UserResponse> createUser(
        UserCreateRequest request,
        MultipartFile profile
    );

    @Operation(summary = "User 정보 수정")
    @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨")
    ResponseEntity<UserResponse> updateUser(
        UUID userId,
        UserUpdateRequest request,
        MultipartFile profile
    );

    @Operation(summary = "User 삭제")
    @ApiResponse(responseCode = "204", description = "User 삭제 성공")
    ResponseEntity<Void> deleteUser(UUID userId);

    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
    ResponseEntity<List<UserResponse>> findAll();

    @Operation(summary = "User 온라인 상태 업데이트")
    @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨")
    ResponseEntity<UserStatusResponse> updateUserStatus(
        UUID userId,
        UserStatusUpdateRequest request
    );
}