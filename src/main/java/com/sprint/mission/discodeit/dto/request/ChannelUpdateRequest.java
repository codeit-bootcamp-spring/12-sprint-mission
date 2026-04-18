package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor

public class ChannelUpdateRequest {
    @Nullable
    private final String newName;
    @Nullable
    private final String newDescription;
}
