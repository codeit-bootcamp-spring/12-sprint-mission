package com.sprint.mission.discodeit.dto.data;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

  private int code;
  private String message;
  private LocalDateTime timestamp;
}
