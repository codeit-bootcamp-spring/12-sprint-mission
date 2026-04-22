package com.sprint.mission.discodeit.dto.User;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;

import java.util.UUID;

// 요청
public record UserUpdateRequest(
        UUID id, // 이부분 넣을지 빼야할 지 모르겠습니다. 요구사항에는 수정 대상 객체의 Id 파라미터 이ㅣㅆ어서 일단 넣었습니다.
        String username,
        String email,
        String password,
        String nickname,
        BinaryContentCreateRequest profileImage
) {
}