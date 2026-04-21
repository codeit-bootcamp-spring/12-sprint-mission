package com.sprint.mission.discodeit.dto.readstatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatusUpdateRequest {
    private UUID readStatusId;
}