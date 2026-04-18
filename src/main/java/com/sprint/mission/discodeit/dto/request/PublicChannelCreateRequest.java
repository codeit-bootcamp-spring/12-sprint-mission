package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PublicChannelCreateRequest {
    private final String name;
    private final String description;

    public Iterable<Object> getParticipantIds() {
        return null;
    }
}
