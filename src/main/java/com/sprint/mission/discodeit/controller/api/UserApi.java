package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "User 관리 API")
public interface UserApi {

    @Operation(summary = "User 등록", description = "새로운 유저를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    ResponseEntity<User> create(
            @Parameter(description = "User 생성 정보") UserCreateRequest request,
            @Parameter(description = "User 프로필 이미지") MultipartFile profile
    );

    @Operation(summary = "User 정보 수정", description = "기존 유저의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User 정보가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "400", description = "수정 요청 파라미터 오류"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 유저를 찾을 수 없음")
    })
    ResponseEntity<User> update(
            @Parameter(description = "수정할 User ID") UUID userId,
            @Parameter(description = "수정할 User 정보") UserUpdateRequest userUpdateRequest,
            @Parameter(description = "수정할 User 프로필 이미지") MultipartFile profile
    );

    @Operation(summary = "User 삭제", description = "특정 유저를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 유저가 존재하지 않음")
    })
    ResponseEntity<Void> delete(@Parameter(description = "삭제할 User ID") UUID userId);

    @Operation(summary = "전체 User 목록 조회", description = "시스템에 등록된 모든 유저를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
    ResponseEntity<List<UserDto>> findAll();

    @Operation(summary = "User 온라인 상태 업데이트", description = "유저의 접속 상태를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
    })
    ResponseEntity<UserStatus> updateUserStatusByUserId(
            @Parameter(description = "상태를 변경할 User ID") UUID userId,
            @RequestBody UserStatusUpdateRequest request
    );
}