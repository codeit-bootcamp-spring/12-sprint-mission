package com.sprint.mission.discodeit.dto.request;

import java.util.List;

import com.sprint.mission.discodeit.entity.User;

public record ChannelCreatePrivateRequestDto(
	List<User> users) {
}
