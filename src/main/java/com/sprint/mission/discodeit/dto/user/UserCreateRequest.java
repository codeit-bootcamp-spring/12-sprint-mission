package com.sprint.mission.discodeit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    private String username;
    private String email;
    private String password;
    private String nickname;

    // 선택적 프로필 이미지
    private String fileName;
    private String contentType;
    private byte[] data;
}