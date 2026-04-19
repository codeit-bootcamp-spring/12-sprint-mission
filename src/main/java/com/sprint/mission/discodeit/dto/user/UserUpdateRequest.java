package com.sprint.mission.discodeit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    private UUID userId;

    private String username;
    private String email;
    private String password;
    private String nickname;

    // 선택적 이미지 변경
    private String fileName;
    private String contentType;
    private byte[] data;
}