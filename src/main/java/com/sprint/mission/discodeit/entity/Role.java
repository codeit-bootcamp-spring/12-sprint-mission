package com.sprint.mission.discodeit.entity;

// 권한 계층: ADMIN > CHANNEL_MANAGER > USER (계층 정의는 SecurityConfig.roleHierarchy)
public enum Role {
  ADMIN,
  CHANNEL_MANAGER,
  USER
}
