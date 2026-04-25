package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String name;
    private String message;

    public static ErrorResponse of(ErrorCode errorCode, String customMessage){
        return new ErrorResponse(errorCode.name(), customMessage);
    }

}
