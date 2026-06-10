package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "status", ignore = true)
    User toEntity(UserCreateRequest request, BinaryContent profile);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "profile", source = "user.profile")
    @Mapping(target = "online", expression = "java(userStatus != null && userStatus.isOnline())")
    UserResponse toResponse(User user, UserStatus userStatus);

    default UserResponse toResponse(User user) {
        if(user == null) {
            return null;
        }
        return toResponse(user, user.getStatus());
    }
}