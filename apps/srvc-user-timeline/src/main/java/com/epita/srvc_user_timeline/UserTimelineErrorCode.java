package com.epita.srvc_user_timeline;

import com.epita.exchange.error.ErrorCode;
import lombok.Getter;

@Getter
public enum UserTimelineErrorCode implements ErrorCode {
  USER_NOT_FOUND(404, "User with ID %s not found"),
  POST_NOT_FOUND(404, "Post with ID %s not found"),
  UNAUTHORIZED(401, "Unauthorized access"),
  INTERNAL_SERVER_ERROR(500, "Internal server error");

  private final int httpCode;
  private final String messageTemplate;

  UserTimelineErrorCode(int httpCode, String messageTemplate) {
    this.httpCode = httpCode;
    this.messageTemplate = messageTemplate;
  }

  @Override
  public String getMessage(Object... parameters) {
    if (parameters == null || parameters.length == 0) {
      return messageTemplate;
    }
    return String.format(messageTemplate, parameters);
  }
}
